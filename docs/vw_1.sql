 -- **************************
 -- vw_1_attendance
 -- **************************
DROP view IF EXISTS vw_1_attendance;
CREATE view vw_1_attendance as
select  v.*
       ,if(activity_name_complement = 'sr', 1, 0) sr
       ,if(activity_name_complement = 'sg', 1, 0) sg
       ,if(activity_id = 1, 1, 0) sr_direcao_espiritual
       ,if(activity_id = 2, 1, 0) sr_meditacao
       ,if(activity_id = 3, 1, 0) sr_recolhimento
       ,if(resumo_mensal_id in (6,7), 1, 0) sr_curso_basico_doutrina
       ,if(activity_id = 5, 1, 0) sr_catequese
       ,if(resumo_mensal_id in (10,12,14), 1, 0) sr_circulo
       ,if(activity_id = 7, 1, 0) sr_retiro
       ,if(activity_id = 8, 1, 0) sr_convivio
       ,if(activity_id = 9, 1, 0) sr_vpv
from vw_attendance as v
where v.center_id = 1;


  -- **************************
  -- vw_1_resumo_sr
  -- **************************
DROP view IF EXISTS vw_1_resumo_sr;
CREATE view vw_1_resumo_sr as
select * from vw_resumo_sr
where center_id in ('center_id', '1');


 -- **************************
 -- vw_1_person
 -- **************************
DROP view IF EXISTS vw_1_person;
CREATE view vw_1_person as
select  p.*
       ,(select max(date) from vw_attendance where person_id = p.id and activity_name_complement = 'sr') sr
       ,(select max(date) from vw_attendance where person_id = p.id and activity_name_complement = 'sg') sg
       ,(select max(date) from attendance where person_id = p.id and activity_id = 1) sr_direcao_espiritual
       ,(select max(date) from attendance where person_id = p.id and activity_id = 2) sr_meditacao
       ,(select max(date) from attendance where person_id = p.id and activity_id = 3) sr_recolhimento
       ,(select max(date) from attendance where person_id = p.id and activity_id = 4) sr_curso_basico_doutrina
       ,(select max(date) from attendance where person_id = p.id and activity_id = 5) sr_catequese
       ,(select max(date) from attendance where person_id = p.id and activity_id in (6,10,11)) sr_circulo
       ,(select max(date) from attendance where person_id = p.id and activity_id = 7) sr_retiro
       ,(select max(date) from attendance where person_id = p.id and activity_id = 8) sr_convivio
       ,(select max(date) from attendance where person_id = p.id and activity_id = 9) sr_vpv
from vw_person p
where center_id = 1;


-- **************************
 -- vw_1_person_count
 -- **************************
DROP view IF EXISTS vw_1_person_count;
CREATE view vw_1_person_count as
          select 01 id, 'ps' type, count(*) total, '01. Nomes' aspect from vw_person where center_id = 1
union all select 02 id, 'ps' type, count(*) total, '02. NEW' aspect from vw_person where center_id = 1 and `status` = 0
union all select 03 id, 'ps' type, count(*) total, '03. Nomes curtos' aspect from vw_person where center_id = 1 and short_name is not null
union all select 04 id, 'sr' type, count(*) total, '04. Universitários' aspect from vw_person where center_id = 1 and check_universitario = 1
union all select 05 id, 'sr' type, count(*) total, '05. Colegiais' aspect from vw_person where center_id = 1 and check_colegial = 1
union all select 06 id, 'ps' type, count(*) total, '06. Com telefone' aspect from vw_person where center_id = 1 and phone is not null
union all select 07 id, 'ps' type, count(*) total, '07. Sem telefone' aspect from vw_person where center_id = 1 and phone is null
union all select 08 id, 'ps' type, count(*) total, '08. Com aniversário' aspect from vw_person where center_id = 1 and birthday is not null
union all select 09 id, 'ps' type, count(*) total, '09. Sem aniversário' aspect from vw_person where center_id = 1 and birthday is null
union all select 10 id, 'ps' type, count(*) total, '10. Com e-mail' aspect from vw_person where center_id = 1 and email is not null
union all select 11 id, 'ps' type, count(*) total, '11. Sem e-mail' aspect from vw_person where center_id = 1 and email is null
union all select 12 id, 'sr' type, count(*) total, '12. Lista do WhatsApp de sr' aspect from vw_person where center_id = 1 and check_estudantewa = 1
union all select 13 id, 'sr' type, count(*) total, '13. Lista de e-mail de sr' aspect from vw_person where center_id = 1 and check_estudante_mail = 1
union all select 14 id, 'sg' type, count(*) total, '14. Cooperadores' aspect from vw_person where center_id = 1 and check_cooperador = 1
union all select 15 id, 'sg' type, count(*) total, '15. Cooperadores que contribuem (R$)' aspect from vw_person where center_id = 1 and check_contribui = 1
union all select 16 id, 'sg' type, count(*) total, '16. Lista do WhatsApp de sg' aspect from vw_person where center_id = 1 and check_profissionalwa = 1
union all select 17 id, 'sg' type, count(*) total, '17. Lista de e-mail de sg' aspect from vw_person where center_id = 1 and check_profissional_mail = 1;
