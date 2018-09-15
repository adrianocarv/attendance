 -- **************************
 -- vw_attendance
DROP view IF EXISTS vw_attendance;
 -- **************************
CREATE view vw_attendance as
select  t.id attendance_id
       ,t.date
       ,t.activity_id
       ,t.person_id
       ,t.title
       ,CASE WHEN title is null THEN DATE_FORMAT(t.date,'%Y/%m/%d (%a)') ELSE CONCAT(DATE_FORMAT(t.date,'%Y/%m/%d (%a)')," - ",title) END date_and_title
       ,last_edit_time
       ,last_edit_user_id
       ,u2.name last_edit_user_name
       ,a.description activity_description
       ,a.name activity_name
       ,a.name_complement activity_name_complement
       ,a.resumo_mensal_id
       ,p.name
       ,p.short_name
       ,p.birthday
       ,p.phone
       ,p.email
       ,if(p.check_colegial = 1, '1','0') check_colegial
       ,if(p.check_universitario = 1, '1','0') check_universitario
       ,p.status
       ,p.tag1
       ,p.tag2
       ,p.tag3
       ,p.tag4
       ,if(p.check_contribui = 1, '1','0') check_contribui
       ,p.check_contribui_value
       ,if(p.check_cooperador = 1, '1','0') check_cooperador
       ,p.check_cooperador_date
       ,if(p.check_estudante_mail = 1, '1','0') check_estudante_mail
       ,p.check_estudante_mail_date
       ,if(p.check_estudantewa = 1, '1','0') check_estudantewa
       ,p.check_estudantewadate
       ,if(p.check_profissional_mail = 1, '1','0') check_profissional_mail
       ,p.check_profissional_mail_date
       ,if(p.check_profissionalwa = 1, '1','0') check_profissionalwa
       ,p.check_profissionalwadate
       ,c.id center_id
       ,c.description center_description
       ,c.name center_name
       ,c.owner_id
       ,u.email owner_email
       ,u.name owner_name
       ,u.username owner_username
from attendance t, activity a, person p, center c, user u, user u2
where t.activity_id = a.id
and t.person_id = p.id
and a.center_id = c.id
and c.owner_id = u.id
and t.last_edit_user_id = u2.id;

 -- **************************
 -- vw_resumo_mensal_inner
 -- **************************
DROP view IF EXISTS vw_resumo_mensal_inner;
CREATE view vw_resumo_mensal_inner as
SELECT   distinct center_id, date_format(date, '%Y') ano, date_format(date, '%Y/%m') ano_mes, a.person_id, name nome, check_universitario, check_colegial, r.*
from     vw_attendance a, resumo_mensal r
where    r.id = resumo_mensal_id;


 -- **************************
 -- vw_resumo_mensal_avg
 -- **************************
DROP view IF EXISTS vw_resumo_mensal_avg;
CREATE view vw_resumo_mensal_avg as
SELECT   center_id, date_format(date, '%Y') ano, date_format(date, '%Y/%m') ano_mes, a.person_id, name nome, check_universitario, check_colegial, r.*
from     vw_attendance a, resumo_mensal r
where    r.id = resumo_mensal_id;


  -- **************************
  -- vw_resumo_sr
  -- **************************
DROP view IF EXISTS vw_resumo_sr;
CREATE view vw_resumo_sr as
select   'center_id', 'ano', 'ano_mes', id, numero, letra, descricao, letra_descricao_nota, descricao_nota, 0 total 
from     resumo_mensal
where    labor = 'sr'

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 4
and      check_universitario = 1
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, (id+1), numero, 'b', (select descricao from resumo_mensal where id = (v.id+1)), letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner v
where    id = 4
and      check_colegial = 1
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 6
and      check_universitario = 1
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, (id+1), numero, 'b', (select descricao from resumo_mensal where id = (v.id+1)), letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner v
where    id = 6
and      check_colegial = 1
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 10
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 12
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 14
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 15
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 16
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, round(count(*)/4) media
from     vw_resumo_mensal_avg
where    id = 17
and      check_universitario = 1
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, (id+1), numero, 'b', (select descricao from resumo_mensal where id = (v.id+1)), letra_descricao_nota, descricao_nota, round(count(*)/4) media
from     vw_resumo_mensal_avg v
where    id = 17
and      check_colegial = 1
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 20
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 22
and      check_universitario = 1
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, (id+1), numero, 'b', (select descricao from resumo_mensal where id = (v.id+1)), letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner v
where    id = 22
and      check_colegial = 1
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, id, numero, letra, descricao, letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner
where    id = 24
and      check_universitario = 1
GROUP BY center_id, ano_mes
union ALL
select   center_id, ano, ano_mes, (id+1), numero, 'b', (select descricao from resumo_mensal where id = (v.id+1)), letra_descricao_nota, descricao_nota, count(*) total
from     vw_resumo_mensal_inner v
where    id = 24
and      check_colegial = 1
GROUP BY center_id, ano, ano_mes

union ALL
select   center_id, ano, ano_mes, 1, '' numero, '' letra, '* SEM LABOR' descricao, '' letra_descricao_nota, '' descricao_nota, count(*)
from     vw_resumo_mensal_inner i
where    i.id in (select id from resumo_mensal)
and      check_universitario <> 1
and      check_colegial <> 1
GROUP BY center_id, ano, ano_mes
union ALL
select   center_id, ano, ano_mes, 1, '' numero, '' letra, '* U + C' descricao, '' letra_descricao_nota, '' descricao_nota, count(*)
from     vw_resumo_mensal_inner i
where    i.id in (select id from resumo_mensal)
and      check_universitario = 1
and      check_colegial = 1
GROUP BY center_id, ano, ano_mes;


 -- **************************
 -- vw_person
 -- **************************
DROP view IF EXISTS vw_person;
CREATE view vw_person as
select  p.id
       ,p.center_id
       ,p.name
       ,p.short_name
       ,p.birthday
       ,p.phone
       ,p.email
       ,if(p.check_colegial = 1, '1','0') check_colegial
       ,if(p.check_universitario = 1, '1','0') check_universitario
       ,p.status
       ,p.tag1
       ,p.tag2
       ,p.tag3
       ,p.tag4
       ,if(p.check_contribui = 1, '1','0') check_contribui
       ,p.check_contribui_value
       ,if(p.check_cooperador = 1, '1','0') check_cooperador
       ,p.check_cooperador_date
       ,if(p.check_estudante_mail = 1, '1','0') check_estudante_mail
       ,p.check_estudante_mail_date
       ,if(p.check_estudantewa = 1, '1','0') check_estudantewa
       ,p.check_estudantewadate
       ,if(p.check_profissional_mail = 1, '1','0') check_profissional_mail
       ,p.check_profissional_mail_date
       ,if(p.check_profissionalwa = 1, '1','0') check_profissionalwa
       ,p.check_profissionalwadate
from person p;
