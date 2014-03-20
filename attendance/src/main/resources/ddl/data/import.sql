--导入ativity（course_date sysdate? course_hour2)
insert into T_ATTEND_ACTIVITIES(id,semester_id,lesson_id,course_id,class_name,course_date,begin_time,end_time,room_id,course_hours)
select a.id,rw.jxrlid,a.jxrwid,kc.id,a.bjmc,sysdate,kqkssj,kqjssj,jsid,2 from attendgather a,jcxx_kc_t kc,jxrw_t  rw
where kc.kcdm =a.kcbh and rw.id=a.jxrwid;


insert into t_attend_details201403(id,std_id,activity_id,attend_type_id)
select a.id,a.xsid,b.id,2 from skmdgather a ,attendgather  b where 
a.jxrwid=b.jxrwid 