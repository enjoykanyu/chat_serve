-- 单聊消息表
create table kanyuServer.chat_db
(
    id              bigint auto_increment comment '主键'
        primary key,
    send_user_id    bigint unsigned                            not null comment '发送消息者id',
    receive_user_id bigint unsigned                            not null comment '接收消息者id',
    message         varchar(255)                               not null comment '发送消息内容',
    is_read         tinyint unsigned default '0'               not null comment '是否被对方已读 0未读 1 已读',
    create_time     timestamp        default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     timestamp        default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    session_id      varchar(255)                               null
);

-- 群聊表

create table kanyuServer.chat_group
(
    id          bigint auto_increment comment '主键id'
        primary key,
    group_name  varchar(100)                                    not null comment '群名称',
    owner_id    bigint unsigned                                 not null comment '群主ID',
    avatar      varchar(255) default 'https://picsum.photos/60' null comment '群头像',
    create_time timestamp    default CURRENT_TIMESTAMP          not null,
    update_time datetime     default CURRENT_TIMESTAMP          null on update CURRENT_TIMESTAMP,
    group_id    varchar(255)                                    not null,
    status      bigint       default 1                          null,
    constraint chat_group_group_id_uindex
        unique (group_id)
);
-- 好友请求消息表
create table kanyuServer.friend_request
(
    id           bigint auto_increment
        primary key,
    requester_id bigint unsigned                    not null,
    receiver_id  bigint unsigned                    not null,
    reason       varchar(200)                       not null,
    status       int      default 0                 null,
    create_time  datetime default CURRENT_TIMESTAMP null,
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint unique_request
        unique (requester_id, receiver_id),
    constraint friend_request_ibfk_1
        foreign key (requester_id) references kanyuServer.user_db (id),
    constraint friend_request_ibfk_2
        foreign key (receiver_id) references kanyuServer.user_db (id)
);

create index receiver_id
    on kanyuServer.friend_request (receiver_id);




-- 好友关系表

create table kanyuServer.friendship
(
    id          bigint auto_increment
        primary key,
    user_id     bigint unsigned                    not null,
    friend_id   bigint unsigned                    not null,
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null,
    status      int      default 1                 null,
    constraint unique_friendship
        unique (user_id, friend_id),
    constraint friendship_ibfk_1
        foreign key (user_id) references kanyuServer.user_db (id),
    constraint friendship_ibfk_2
        foreign key (friend_id) references kanyuServer.user_db (id)
);

create index friend_id
    on kanyuServer.friendship (friend_id);



-- 群聊成员表
create table kanyuServer.group_member
(
    id        bigint auto_increment
        primary key,
    group_id  varchar(255)                        not null comment '群组ID',
    user_id   bigint unsigned                     not null comment '成员ID',
    join_time timestamp default CURRENT_TIMESTAMP not null,
    role      tinyint   default 0                 null comment '成员角色 0-普通 1-管理员',
    status    bigint    default 1                 null
)
    comment '群成员表';

create index group_id
    on kanyuServer.group_member (group_id);

-- 群聊消息表
create table kanyuServer.group_message
(
    id          bigint auto_increment
        primary key,
    group_id    varchar(255)                        not null comment '群组ID',
    sender_id   varchar(255)                        not null comment '发送者ID',
    content     varchar(255)                        not null comment '消息内容',
    create_time timestamp default CURRENT_TIMESTAMP not null,
    type        int                                 null,
    update_time timestamp default CURRENT_TIMESTAMP null
)
    comment '群消息表';

create index group_message_ibfk_1
    on kanyuServer.group_message (group_id);



-- 用户信息表
create table kanyuServer.user_db
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    phone       varchar(11)                                     not null comment '手机号码',
    password    varchar(128) default ''                         null comment '密码，加密存储',
    user_name   varchar(32)  default ''                         null comment '昵称，默认是用户id',
    create_time timestamp    default CURRENT_TIMESTAMP          not null comment '创建时间',
    update_time timestamp    default CURRENT_TIMESTAMP          not null on update CURRENT_TIMESTAMP comment '更新时间',
    avatar      varchar(255) default 'https://picsum.photos/60' null,
    created_at  datetime                                        null,
    updated_at  datetime                                        null,
    deleted_at  datetime                                        null,
    username    varchar(64)                                     not null,
    constraint uniqe_key_phone
        unique (phone)
);



create index idx_user_db_deleted_at
    on kanyuServer.user_db (deleted_at);

