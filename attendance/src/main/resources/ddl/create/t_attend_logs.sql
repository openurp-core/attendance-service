create table t_attend_logs${postfix}(dev_id number(5),card_id varchar2(20),signin_at date,remark varchar2(200));

comment on table t_attend_logs${postfix} is '${postfix}考勤流水表';
comment on column t_attend_logs${postfix}.dev_id is '设备ID';
comment on column t_attend_logs${postfix}.card_id is '出勤人员工学号';
comment on column t_attend_logs${postfix}.signin_at is '签到日期和时间';
comment on column t_attend_logs${postfix}.remark is '备注';
