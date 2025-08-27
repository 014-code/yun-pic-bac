package com.mashang.yunbac.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.UserRoleEnum;
import com.mashang.yunbac.web.entity.params.common.PageInfoParam;
import com.mashang.yunbac.web.entity.params.picture.CaptureParam;
import com.mashang.yunbac.web.entity.params.picture.GetPictrueListParam;
import com.mashang.yunbac.web.entity.vo.picture.UploadPictureResult;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureVo;
import com.mashang.yunbac.web.entity.vo.user.YunUserVo;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.manger.FileManger;
import com.mashang.yunbac.web.manger.RedisManger;
import com.mashang.yunbac.web.mapper.YunPictureMapper;
import com.mashang.yunbac.web.service.YunPictureService;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.utils.ResultTUtil;
import com.mashang.yunbac.web.utils.RowsTUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (YunPicture)表服务实现类
 *
 * @author makejava
 * @since 2025-08-19 20:51:56
 */
@Service("yunPictureService")
public class YunPictureServiceImpl extends ServiceImpl<YunPictureMapper, YunPicture> implements YunPictureService {

    @Resource
    private YunPictureMapper yunPictureMapper;
    @Autowired
    private FileManger fileManger;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisManger redisManger;

    //本地缓存构造(五分钟过期时间 + 最大存储10000条数据)
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder().initialCapacity(1024).maximumSize(10000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();

    /**
     * 上传图片
     *
     * @param file
     * @param picId
     * @param yunUser
     * @return
     */
    @Override
    public ResultTUtil<YunPictureVo> uploadPic(Object file, Long picId, YunUser yunUser, String picName) {
        //判断传入的用户信息是否有登录，没则直接抛异常
        ThrowUtils.throwIf(yunUser == null, ErrorCode.NOT_LOGIN_ERROR);
        //判断是新增图片还是更新图片-有id就是更新,如果是更新则需要校验图片是否存在，不存在也要抛异常
        if (picId != null) {
            //库表查是否存在
            QueryWrapper<YunPicture> yunPictureQueryWrapper = new QueryWrapper<>();
            yunPictureQueryWrapper.eq("pic_id", picId).eq("user_id", yunUser.getUserId());
            YunPicture yunPicture = yunPictureMapper.selectOne(yunPictureQueryWrapper);
            ThrowUtils.throwIf(yunPicture == null, ErrorCode.NOT_FOUND_ERROR);
        }
        //使用filemanger得上传图片，还得划分目录，这里划分至public目录即可 + 用户id
        String format = "public/" + yunUser.getUserId();
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        //区分上传的是文件类型还是url
        if (file instanceof MultipartFile) {
            //把从filemanger得到的图片信息入库-更新或者新增
            uploadPictureResult = fileManger.uploadPicture((MultipartFile) file, format);
        } else if (file instanceof String) {
            //把从filemanger得到的图片信息入库-更新或者新增
            uploadPictureResult = fileManger.uploadPicture((String) file, format, false);
        }

        YunPicture yunPicture = new YunPicture();
        yunPicture.setUserId(yunUser.getUserId());
        //如果有指定名称
        if (picName != null) {
            yunPicture.setName(picName);
        } else {
            yunPicture.setName(uploadPictureResult.getPicName());
        }
        yunPicture.setIntroduction("该图片暂无简介");
        yunPicture.setPicSize(uploadPictureResult.getPicSize());
        yunPicture.setPicWidth(uploadPictureResult.getPicWidth());
        yunPicture.setPicHeight(uploadPictureResult.getPicHeight());
        yunPicture.setPicScale(uploadPictureResult.getPicScale());
        yunPicture.setPicFormat(uploadPictureResult.getPicFormat());
        yunPicture.setCreateTime(new Date());
        yunPicture.setUpdateTime(new Date());
        yunPicture.setUrl(uploadPictureResult.getUrl());
        //为管理员则直接通过
        if (yunUser.getRole() != null && yunUser.getRole().equals(UserRoleEnum.ADMIN.getValue())) {
            yunPicture.setStatus("1");
        }
        //是否为更新
        if (picId != null) {
            yunPicture.setPicId(picId);
            yunPictureMapper.updateById(yunPicture);
        } else {
            yunPictureMapper.insert(yunPicture);
            yunPicture.setPicId(yunPictureMapper.selectById(yunPicture.getPicId()).getPicId());
        }
        YunPictureVo yunPictureVo = new YunPictureVo();
        BeanUtils.copyProperties(yunPicture, yunPictureVo);
        yunPictureVo.setPicId(yunPicture.getPicId());
        return new ResultTUtil<YunPictureVo>().success("查询成功", yunPictureVo);
    }

    /**
     * 批量抓取图片
     *
     * @param captureParam
     * @return
     */
    @Override
    public ResultTUtil capture(CaptureParam captureParam, YunUser yunUserVo) {
        Long num = captureParam.getNum();
        String text = captureParam.getText();
        String prefix = captureParam.getPrefix();
        //拿到抓取地址
        String format = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", text);
        Document document;
        try {
            //抓取对应地址
            document = Jsoup.connect(format).get();
        } catch (IOException e) {
            log.error(String.valueOf(e));
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取页面失败");
        }
        //拿到带有这个类名dom元素
        Element dgControl = document.getElementsByClass("dgControl").first();
        if (dgControl == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取元素失败");
        }
        //css选择器
        Elements select = dgControl.select(".iuscp.isv");
        Long uploadCount = 1L;
        Long successCount = 0L;
        Long failCount = 0L;

        for (Element element : select) {
            try {
                String src = element.select(".iusc").get(0).attr("m");
                //转义地址-?所在的下标
                Pattern pattern = Pattern.compile("\"murl\":\"(.*?)\"");
                Matcher matcher = pattern.matcher(src);
                if (matcher.find()) {
                    src = matcher.group(1);
                }
                if (src == null || src.equals("")) {
                    log.error(String.valueOf(element));
                    continue;
                }
                int i = src.indexOf("?");
                if (i != -1) {
                    //切割地址
                    src = src.substring(0, i);
                }
                String pciName = null;
                //名称默认为搜索词
                if (prefix == null) {
                    pciName = text;
                } else {
                    pciName = prefix;
                }
                //调用url上传，跳过验证避免网络问题
                uploadPic(src, null, yunUserVo, pciName + uploadCount);
                uploadCount++;
                successCount++;
                //数量达到则跳出
                if (uploadCount > num) {
                    break;
                }
            } catch (Exception e) {
                failCount++;
//                log.warn("处理图片失败，跳过: {}, 错误: {}", element, e.getMessage());
                continue; // 继续处理下一个
            }
        }

        String resultMsg = String.format("操作完成，成功: %d, 失败: %d", successCount, failCount);
        return new ResultTUtil().success(resultMsg);
    }

//    @Override
//    public RowsTUtil<YunPictureVo> listVo(PageInfoParam pageInfoParam, GetPictrueListParam getPictrueListParam) {
//        //构造redis的key
//        String queryCondition = JSONUtil.toJsonStr(getPictrueListParam);
//        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
//        String redisKey = "yunpic:listVo:" + hashKey;
//        //1.先查询本地缓存
//        String ifPresent = LOCAL_CACHE.getIfPresent(redisKey);
//        if (ifPresent != null) {
//            List<YunPictureVo> bean = JSONUtil.toBean(ifPresent, List.class);
//            return new RowsTUtil<YunPictureVo>().success("查询成功", (long) bean.size(), bean);
//        }
//        //2.查询redis中的
//        //拿到redis的string操作对象，并指定map类型
//        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//        String cached = stringStringValueOperations.get(redisKey);
//        //如果有则将其解析化为对象
//        if (cached != null) {
//            List<YunPictureVo> bean = JSONUtil.toBean(stringStringValueOperations.get(cached), List.class);
//            //存入本地缓存
//            LOCAL_CACHE.put(redisKey, cached);
//            return new RowsTUtil<YunPictureVo>().success("查询成功", (long) bean.size(), bean);
//        }
//        List<YunPictureVo> yunPictureVos = selectList(pageInfoParam, getPictrueListParam);
//        PageInfo<YunPicture> pageList = getPage(pageInfoParam, getPictrueListParam);
//        //变成json字符串
//        String jsonStr = JSONUtil.toJsonStr(yunPictureVos);
//        //设置过期时间
//        int cacheTime = RandomUtil.randomInt(0, 300);
//        //缓存入redis
//        stringStringValueOperations.set(redisKey, jsonStr, cacheTime, TimeUnit.SECONDS);
//        //更新本地缓存
//        LOCAL_CACHE.put(redisKey, jsonStr);
//        return new RowsTUtil<YunPictureVo>().success("查询成功", pageList.getTotal(), yunPictureVos);
//    }

    /**
     * 查询数据库
     *
     * @param pageInfoParam
     * @param getPictrueListParam
     */
    private List<YunPictureVo> selectList(PageInfoParam pageInfoParam, GetPictrueListParam getPictrueListParam) {
        List<YunPicture> list = getList(pageInfoParam, getPictrueListParam);
        //转化脱敏
        // 转换为VO列表
        List<YunPictureVo> yunPictureVos = new ArrayList<>();
        for (YunPicture yunUser : list) {
            YunPictureVo vo = new YunPictureVo();
            BeanUtil.copyProperties(yunUser, vo);
            yunPictureVos.add(vo);
        }
        return yunPictureVos;
    }

    /**
     * 获取分页信息方法
     *
     * @return
     */
    private PageInfo<YunPicture> getPage(PageInfoParam pageInfoParam, GetPictrueListParam getPictrueListParam) {
        List<YunPicture> list = getList(pageInfoParam, getPictrueListParam);
        // 获取分页信息
        PageInfo<YunPicture> pageList = new PageInfo<>(list);
        return pageList;
    }

    /**
     * 获取原始列表信息
     *
     * @param pageInfoParam
     * @param getPictrueListParam
     */
    private List<YunPicture> getList(PageInfoParam pageInfoParam, GetPictrueListParam getPictrueListParam) {
        // 开启分页
        PageHelper.startPage(pageInfoParam.getPageNum(), pageInfoParam.getPageSize());
        //后台列表根据多个条件查询
        QueryWrapper<YunPicture> yunUserVoQueryWrapper = new QueryWrapper<>();
        yunUserVoQueryWrapper.like(getPictrueListParam.getCategory() != null, "category", getPictrueListParam.getCategory());
        yunUserVoQueryWrapper.like(getPictrueListParam.getName() != null, "name", getPictrueListParam.getName());
        yunUserVoQueryWrapper.like(getPictrueListParam.getTags() != null, "tags", getPictrueListParam.getTags());
        yunUserVoQueryWrapper.like(getPictrueListParam.getIntroduction() != null, "introduction", getPictrueListParam.getIntroduction());
        yunUserVoQueryWrapper.like(getPictrueListParam.getPicFormat() != null, "pic_format", getPictrueListParam.getPicFormat());
        yunUserVoQueryWrapper.eq("status", "1");
        List<YunPicture> list = yunPictureMapper.selectList(yunUserVoQueryWrapper);
        return list;
    }

    /**
     * 解决缓存击穿的互斥锁
     *
     * @return
     */
    public RowsTUtil<YunPictureVo> listVo(PageInfoParam pageInfoParam, GetPictrueListParam getPictrueListParam) {
        List<YunPictureVo> yunPictureVos = new ArrayList<>();
        //构造redis的key
        String queryCondition = JSONUtil.toJsonStr(getPictrueListParam);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String key = "yunpic:listVo:" + hashKey;
        //从Redis查询缓存
        String picJson = stringRedisTemplate.opsForValue().get(key);  //JSON格式
        //判断是否存在
        if (StrUtil.isNotBlank(picJson)) {
            //存在则直接返回
            List<YunPictureVo> bean = JSONUtil.toBean(picJson, List.class);
            return new RowsTUtil<YunPictureVo>().success("查询成功", (long) bean.size(), bean);
        }
        ThrowUtils.throwIf(picJson != null, ErrorCode.NOT_FOUND_ERROR);
        //4.缓存重建
        //4.1获得互斥锁
        String lockKey = "lock:shop" + hashKey;
        List<YunPictureVo> picList = null;
        try {
            // 尝试获取锁，等待10秒，锁持有30秒
            boolean isLock = redisManger.tryLock(lockKey, 10, 30, TimeUnit.SECONDS);
            //判断是否获取成功
            if (!isLock) {
                //4.3失败，则休眠并重试
                Thread.sleep(50);
                return listVo(pageInfoParam, getPictrueListParam);
            }
            //成功查询数据库
            yunPictureVos.addAll(selectList(pageInfoParam, getPictrueListParam));
            //设置过期时间
            int cacheTime = RandomUtil.randomInt(0, 300);
            //不存在则返回错误
            if (yunPictureVos == null) {
                //将空值写入Redis
                stringRedisTemplate.opsForValue().set(key, "", cacheTime, TimeUnit.SECONDS);
                //return Result.fail("暂无该商铺信息");
                ThrowUtils.throwIf(yunPictureVos == null, ErrorCode.NOT_FOUND_ERROR);
            }
            //存在，写入Redis
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(yunPictureVos), cacheTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //释放互斥锁
            redisManger.unlock(lockKey);
        }
        PageInfo<YunPicture> pageList = getPage(pageInfoParam, getPictrueListParam);
        //返回分页对象
        return new RowsTUtil<YunPictureVo>().success("查询成功", pageList.getTotal(), yunPictureVos);
    }
}
