-- 创建库
create database if not exists alsap;

-- 切换库
use alsap;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- cti信息表
create table if not exists cti
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    userId     bigint                             null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment 'cti提交表' collate = utf8mb4_unicode_ci;

-- 展示的是分块的cti信息，他可以被实体切分为很多块 每一次更新最好的方法就是删除关于这个cti的所有数据，这样更新会好一点
create table if not exists cti_chunk
(
    id          bigint auto_increment comment 'id' primary key,
    ctiId       bigint                             null comment 'cti情报表的ID',
    itemId      bigint                             null comment '实体id，如果前端没有传，那么就是对应O的item',
    sentText    text                               null comment '属于这一块的文本信息（句子）',
    userId      bigint                             null comment '创建这个实体的用户ID',
    startOffset int                                null comment '在文本中的开始位置',
    endOffset   int                                null comment '在文本中的结束位置',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment 'cti提交表' collate = utf8mb4_unicode_ci;


-- 实体信息表
create table if not exists item
(
    id              bigint auto_increment comment 'id' primary key,
    itemName        varchar(512)                       null comment '实体的名称',
    itemContent     text                               null comment '实体的解释',
    backgroundColor varchar(512)                       not null comment '实体标注时前端展示的背景颜色',
    textColor       varchar(512)                       not null comment '实体标注时前端展示的字体颜色',
    itemType        tinyint                            not null comment '实体类型，1是sdo,2是sco',
    itemTypeContent varchar(512)                       not null comment '对itemType的一个解释',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_itemName (itemName)
) comment '实体信息表' collate = utf8mb4_unicode_ci;

INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('domain-name', '#FFE4B5', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('process', '#F4A460', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('url', '#C71585', '#ffffff', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_botnet', '#FFA07A', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('identity_victim', '#FF6347', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('vulnerability', '#E9967A', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_reconnaissance', '#FFEFD5', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('location', '#FFEBCD', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_exfiltration', '#F5DEB3', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('mutex', '#D2B48C', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('file-hash', '#FFB6C1', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('directory', '#FFA500', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_screen-capture', '#FFDEAD', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('threat-actor', '#FF8C00', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('attack-pattern', '#DB7093', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_remote-access-trojan', '#FF69B4', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure', '#FF6347', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_ransomware', '#FFDEAD', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_hosting-malware', '#FFD700', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_webshell', '#F4A460', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('campaign', '#C71585', '#ffffff', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_resource-exploitation', '#FA8072', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_attack', '#FFDAB9', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_victim', '#FF6347', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('intrusion-set', '#FFFF00', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('ipv4-addr', '#FFEFD5', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('file-name', '#FFEBCD', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_virus', '#F5DEB3', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('user-account', '#FFB6C1', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_worm', '#FFA500', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('software', '#FF1493', '#ffffff', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_ddos', '#FFDEAD', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_bot', '#DB7093', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('identity', '#FF69B4', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_keylogger', '#FF6347', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('http-request-ext', '#FFDEAD', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('windows-registry-key', '#FFDAB9', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('tool', '#FFD700', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware', '#C71585', '#ffffff', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('infrastructure_command-and-control', '#FA8072', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('email-addr', '#FFDAB9', '#000000', '2', 'STIX Cyber-observable Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('malware_exploit-kit', '#E9967A', '#000000', '1', 'STIX Domain Object');
INSERT INTO item (itemName, backgroundColor, textColor, itemType, itemTypeContent)
VALUES ('ipv6-addr', '#FFFF00', '#000000', '2', 'STIX Cyber-observable Object');

-- 实体表
create table if not exists entity
(
    id         bigint auto_increment comment 'id' primary key,
    entityName varchar(512)                       null comment '实体内容，内容相同的我就不需要去插入了',
    ctiId      bigint                             null comment '这个实体对应的CTI文章是什么',
    userId     bigint                             null comment '创建用户 id',
    itemId     bigint                             null comment '这个实体对应的itemId，也就是需要知道这实体是什么类型',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '实体表，这是用来存放真实的经过人工确认的' collate = utf8mb4_unicode_ci;

-- 模型实体表
create table if not exists model_entity
(
    id         bigint auto_increment comment 'id' primary key,
    entityName varchar(512)                       null comment '实体内容，内容相同的我就不需要去插入了',
    ctiId      bigint                             null comment '这个实体对应的CTI文章是什么',
    itemId     bigint                             null comment '这个实体对应的itemId，也就是需要知道这实体是什么类型',
    userId     bigint                             null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '模型的实体表，这是用来存放模型的识别结果的' collate = utf8mb4_unicode_ci;

-- 关系表
create table if not exists relation
(
    id                    bigint auto_increment comment 'id' primary key,
    ctiId                 bigint                             null comment '这个实体对应的CTI文章是什么',
    startDetailCtiChunkId bigint                             null comment '头实体Id',
    endDetailCtiChunkId   bigint                             null comment '尾实体Id',
    relationTypeId        bigint                             null comment '关系Id',
    createTime            datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime            datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete              tinyint  default 0                 not null comment '是否删除'
) comment '实体表' collate = utf8mb4_unicode_ci;

-- 描写的是情报sroType的表格
create table if not exists relationType
(
    id                bigint auto_increment comment 'id' primary key,
    startEntityItemId bigint                             null comment '头实体的在item表中的id',
    endEntityItemId   bigint                             null comment '尾实体的在item表中的id',
    relationName      varchar(512)                       null comment '关系的名称',
    createTime        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete          tinyint  default 0                 not null comment '是否删除'
) comment 'stix中规定的关系类型，我们规定好，不同类型的实体时间可以存在什么样的关系' collate = utf8mb4_unicode_ci;
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (15, 39, 'delivers');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (15, 34, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (15, 8, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (15, 6, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (15, 39, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (15, 38, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 25, 'attributed-to');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 14, 'attributed-to');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 17, 'compromises');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 8, 'originates-from');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 34, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 8, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 6, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 15, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 17, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 39, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (21, 38, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (34, 8, 'located-at');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 17, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 26, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 43, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 1, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 3, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 17, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 12, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 1, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 41, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 11, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 27, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 26, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 43, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 36, 'consists-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 17, 'controls');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 39, 'controls');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 39, 'delivers');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 6, 'has');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 38, 'hosts');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 39, 'hosts');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 8, 'located-at');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (17, 17, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 14, 'attributed-to');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 17, 'compromises');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 17, 'hosts');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 17, 'owns');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 8, 'originates-from');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 34, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 8, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 6, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 15, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 17, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 39, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (25, 38, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 14, 'authored-by');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 25, 'authored-by');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 17, 'beacons-to');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 17, 'exfiltrate-to');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 26, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 43, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 1, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 3, 'communicates-with');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 39, 'controls');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 39, 'downloads');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 38, 'downloads');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 39, 'drops');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 38, 'drops');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 6, 'exploits');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 8, 'originates-from');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 34, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 17, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 8, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 6, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 15, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 17, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 39, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 38, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (39, 39, 'variant-of');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 34, 'attributed-to');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 17, 'compromises');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 17, 'hosts');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 17, 'owns');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 34, 'impersonates');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 8, 'located-at');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 34, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 8, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 6, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 8, 'located-at');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 15, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 17, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 39, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (14, 38, 'uses');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 39, 'delivers');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 39, 'drops');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 6, 'has');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 34, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 17, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 8, 'targets');
INSERT INTO relationType (startEntityItemId, endEntityItemId, relationName)
VALUES (38, 6, 'targets');


create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';
