create table t_attend_activities (id number(19), semester_id number(19) not null,lesson_id number(19) not null,
course_id number(10) not null,course_date date,attend_begin_time number(5) not null,begin_time number(5) not null,end_time number(5) not null,
room_id number(10) not null,course_hours number(5) not null);

create sequence seq_t_attend_activities;

comment on table t_attend_activities is '考勤活动表';
comment on column t_attend_activities.id is '非业务主键';
comment on column t_attend_activities.semester_id is '学期ID';
comment on column t_attend_activities.lesson_id is '考勤教学任务ID';
comment on column t_attend_activities.course_id is '课程ID';
comment on column t_attend_activities.course_date is '上课日期';
comment on column t_attend_activities.attend_begin_time is '考勤开始时间';
comment on column t_attend_activities.begin_time is '上课开始时间';
comment on column t_attend_activities.end_time is '上课结束时间';
comment on column t_attend_activities.room_id is '教室ID';
comment on column t_attend_activities.course_hours is '课时';


create index idx_attend_activity_room on t_attend_activities(room_id);
create index idx_attend_activity_lesson on t_attend_activities(lesson_id);
create index idx_attend_activity_date on t_attend_activities(course_date);
create index idx_attend_activity_date_room on t_attend_activities(course_date,room_id);

