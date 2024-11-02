package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.UserNote;
import com.clothrent.mapper.UserNoteMapper;
import com.clothrent.service.UserNoteService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 帖子表 服务实现类
 * </p>
 *
 * @author liuqiming
 * @since 2023-11-18
 */
@Service
public class UserNoteServiceImpl extends ServiceImpl<UserNoteMapper, UserNote> implements UserNoteService {

}
