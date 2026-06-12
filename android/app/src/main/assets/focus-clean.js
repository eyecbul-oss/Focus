const $ = q => document.querySelector(q);
const $$ = q => document.querySelectorAll(q);
const SUBJECTS = ['Matematik','Geometri','Fizik','Kimya','Biyoloji','Türkçe','Tarih','Coğrafya','Felsefe','İngilizce'];
let total = 1500, remain = 1500, run = false, timer = null;
let data = JSON.parse(localStorage.getItem('focus') || '{"profile":{},"reminder":{"on":"off","time":"19:00","note":"Bugün hedefini tamamla"},"tasks":[],"homeworks":[],"min":0,"pom":0,"goal":120,"theme":"dark","exam":"YKS","examDate":"2026-06-20","qGoal":120,"qDone":0,"trial":0,"subjects":{},"sessions":[]}');

function init(){
  data.profile = data.profile || {};
  data.reminder = data.reminder || {on:'off', time:'19:00', note:'Bugün hedefini tamamla'};
  data.tasks = data.tasks || [];
  data.homeworks = data.homeworks || [];
  data.subjects = data.subjects || {};
  data.sessions = data.sessions || [];
  data.goal = data.goal || 120;
  data.theme = data.theme || 'dark';
  data.exam = data.exam || 'YKS';
  data.examDate = data.examDate || defaultExamDate(data.exam);
  data.qGoal = data.qGoal || 120;
  data.qDone = data.qDone || 0;
  data.trial = data.trial || 0;
}
function save(){ localStorage.setItem('focus', JSON.stringify(data)); }
function today(){ return new Date().toISOString().slice(0,10); }
function monthKey(){ return new Date().toISOString().slice(0,7); }
function fmt(x){ return String(Math.floor(x/60)).padStart(2,'0') + ':' + String(x%60).padStart(2,'0'); }
function days(n){ return String(Math.max(0, Math.floor(n))); }
function download(name,text,type){
  let a=document.createElement('a');
  a.href=URL.createObjectURL(new Blob([text],{type}));
  a.download=name;
  document.body.appendChild(a);
  a.click();
  setTimeout(()=>{URL.revokeObjectURL(a.href);a.remove()},500);
}
function setText(id,text){ const el=$('#'+id); if(el) el.textContent=text; }
function setValue(id,value){ const el=$('#'+id); if(el) el.value=value; }
function fillSelect(id,arr){
  let el=$('#'+id);
  if(el && !el.children.length){
    arr.forEach(v=>{ let o=document.createElement('option'); o.value=v; o.textContent=v; el.appendChild(o); });
  }
}
function xpValue(){
  let doneTasks=data.tasks.filter(t=>t.done).length;
  let doneHw=data.homeworks.filter(h=>h.done).length;
  return (data.min||0)*5+(data.pom||0)*20+doneTasks*10+doneHw*15+(data.qDone||0);
}
function reportText(){
  let top=Object.entries(data.subjects||{}).sort((a,b)=>(b[1].min||0)-(a[1].min||0))[0];
  let openHw=data.homeworks.filter(h=>!h.done).length;
  let doneHw=data.homeworks.filter(h=>h.done).length;
  let doneTasks=data.tasks.filter(t=>t.done).length;
  let name=data.profile.name||'Öğrenci';
  return name+' raporu: '+(data.min||0)+' dk çalışma, '+(data.pom||0)+' seans, '+(data.qDone||0)+' soru, '+doneTasks+'/'+data.tasks.length+' görev, '+doneHw+'/'+data.homeworks.length+' ödev tamamlandı. En güçlü ders: '+(top?top[0]+' ('+(top[1].min||0)+' dk)':'henüz yok')+'. Açık ödev: '+openHw+'.';
}
function render(){
  document.body.dataset.theme=data.theme||'dark';
  setText('todayMin', data.min||0);
  setText('todayPom', data.pom||0);
  setText('streak', (data.min||0)>0?1:0);
  let pct=Math.min(100, Math.round((data.min||0)/(data.goal||120)*100));
  setText('score', pct+'%');
  let xp=xpValue();
  setText('xp', xp);
  setText('levelNo', Math.floor(xp/500)+1);
  setText('time', fmt(remain));
  setText('otime', fmt(remain));
  let ring=$('#ring'); if(ring) ring.style.setProperty('--deg', ((total-remain)/total*360)+'deg');
  setValue('dailyGoal', data.goal||120);
  setText('goalText', 'Bugünkü hedef: '+(data.goal||120)+' dk • Tamamlanma: %'+pct);
  let gp=$('#goalProg'); if(gp) gp.style.width=pct+'%';
  renderProfile(); renderReminder(); renderTasks(); renderHomeworks(); renderWeek(); renderOther(); renderReport(); renderMonthly(); renderExam();
}
function renderProfile(){
  if(!$('#studentName')) return;
  setValue('studentName', data.profile.name||'');
  setValue('studentClass', data.profile.className||'');
  setValue('studentGoal', data.profile.goal||'');
  setText('profileText', (data.profile.name||'Öğrenci')+' • '+(data.profile.className||'Sınıf belirtilmedi')+' • Hedef: '+(data.profile.goal||'belirtilmedi'));
}
function renderReminder(){
  if(!$('#reminderTime')) return;
  setValue('reminderTime', data.reminder.time||'19:00');
  setValue('reminderNote', data.reminder.note||'Bugün hedefini tamamla');
  setValue('reminderOn', data.reminder.on||'off');
  setText('reminderText', (data.reminder.on==='on'?'Açık':'Kapalı')+' • '+(data.reminder.time||'19:00')+' • '+(data.reminder.note||'Bugün hedefini tamamla'));
}
function renderTasks(){
  let box=$('#tasks'); if(!box) return; box.innerHTML='';
  data.tasks.forEach((t,i)=>{
    let item=document.createElement('div'); item.className='item'+(t.done?' done':'');
    item.innerHTML='<button class="task-check">'+(t.done?'✓':'○')+'</button><span>'+t.text+'</span><button class="task-delete">Sil</button>';
    item.children[0].onclick=()=>{t.done=!t.done; save(); render();};
    item.children[2].onclick=()=>{data.tasks.splice(i,1); save(); render();};
    box.appendChild(item);
  });
  let done=data.tasks.filter(t=>t.done).length, p=data.tasks.length?Math.round(done/data.tasks.length*100):0;
  $('#taskProg').style.width=p+'%'; setText('taskText','Görev ilerlemesi: %'+p+' ('+done+'/'+data.tasks.length+')');
}
function renderHomeworks(){
  let box=$('#homeworks'); if(!box) return; box.innerHTML='';
  data.homeworks.forEach((h,i)=>{
    let item=document.createElement('div'); item.className='item'+(h.done?' done':'');
    let due=h.due?(' • '+h.due):'', topic=h.topic?(' / '+h.topic):'';
    item.innerHTML='<button class="task-check">'+(h.done?'✓':'○')+'</button><span><b>'+h.title+'</b><small>'+h.subject+topic+due+'</small></span><button class="task-delete">Sil</button>';
    item.children[0].onclick=()=>{h.done=!h.done; h.doneDate=h.done?today():''; save(); render();};
    item.children[2].onclick=()=>{data.homeworks.splice(i,1); save(); render();};
    box.appendChild(item);
  });
  let done=data.homeworks.filter(h=>h.done).length, p=data.homeworks.length?Math.round(done/data.homeworks.length*100):0;
  $('#hwProg').style.width=p+'%'; setText('hwText','Ödev ilerlemesi: %'+p+' ('+done+'/'+data.homeworks.length+')');
}
function renderWeek(){
  let w=$('#weekStats'); if(!w) return; w.innerHTML='';
  let names=['Paz','Pzt','Sal','Çar','Per','Cum','Cmt'], now=new Date(), totalWeek=0;
  for(let back=6; back>=0; back--){
    let d=new Date(now); d.setDate(now.getDate()-back); let key=d.toISOString().slice(0,10);
    let min=data.sessions.filter(s=>s.date===key).reduce((a,s)=>a+(s.min||0),0); totalWeek+=min;
    let e=document.createElement('div'); e.className='week-day'; e.innerHTML='<b>'+min+'</b><span>'+names[d.getDay()]+'</span>'; w.appendChild(e);
  }
  setText('weekText','Son 7 gün toplam: '+totalWeek+' dk');
}
function renderSubjects(){
  let box=$('#subjectStats'); if(!box) return;
  let entries=Object.entries(data.subjects||{}).sort((a,b)=>(b[1].min||0)-(a[1].min||0));
  if(!entries.length){ box.innerHTML='<p class="muted">Henüz ders seansı yok. Pomodoro bitince seçili derse işlenecek.</p>'; return; }
  box.innerHTML=''; entries.slice(0,8).forEach(([name,s])=>{
    let row=document.createElement('div'); row.className='subject-row';
    row.innerHTML='<strong>'+name+'</strong><p class="muted">'+(s.min||0)+' dk • '+(s.pom||0)+' seans</p><div class="progress"><span style="width:'+Math.min(100,(s.min||0))+'%"></span></div>';
    box.appendChild(row);
  });
}
function renderHeat(){
  let box=$('#heatmap'); if(!box) return; box.innerHTML='';
  let now=new Date();
  for(let back=29; back>=0; back--){
    let d=new Date(now); d.setDate(now.getDate()-back); let key=d.toISOString().slice(0,10);
    let min=data.sessions.filter(s=>s.date===key).reduce((a,s)=>a+(s.min||0),0);
    let lvl=min>=120?4:min>=60?3:min>=25?2:min>0?1:0;
    let e=document.createElement('span'); e.className='heat '+(lvl?'level-'+lvl:''); e.title=key+' • '+min+' dk'; box.appendChild(e);
  }
}
function renderOther(){
  fillSelect('subjectSelect',SUBJECTS); fillSelect('hwSubject',SUBJECTS);
  fillSelect('themeMode',['dark','blue','forest','light']); fillSelect('soundMode',['silent','soft','classic','alarm']);
  renderSubjects(); renderHeat();
  let done=data.tasks.filter(t=>t.done).length, doneHw=data.homeworks.filter(h=>h.done).length, xp=xpValue();
  $('#badges').innerHTML='<div class="achievement earned"><strong>🎯</strong><b>Odak</b></div><div class="achievement '+(xp>=1000?'earned':'')+'"><strong>🏆</strong><b>1000 XP</b></div><div class="achievement '+((data.pom||0)>=5?'earned':'')+'"><strong>🔥</strong><b>5 Seans</b></div><div class="achievement '+((data.qDone||0)>=100?'earned':'')+'"><strong>📚</strong><b>100 Soru</b></div><div class="achievement '+(doneHw>=5?'earned':'')+'"><strong>✅</strong><b>5 Ödev</b></div>';
  let lv=Math.floor(xp/500)+1, next=lv*500, prev=(lv-1)*500, lp=Math.round((xp-prev)/(next-prev)*100);
  setText('levelTitle', lv<3?'Başlangıç':lv<6?'Düzenli Öğrenci':'Odak Ustası'); setText('levelChip','Lv '+lv); setText('levelText','Sonraki seviye için '+Math.max(0,next-xp)+' XP kaldı.');
  $('#levelProg').style.width=Math.min(100,lp)+'%';
  let qg=data.qGoal||120, qd=data.qDone||0, qp=qg?Math.min(100,Math.round(qd/qg*100)):0;
  setValue('questionGoal',qg); setValue('questionDone',qd); setValue('trialCount',data.trial||0); setText('yksText','Soru hedefi: '+qd+'/'+qg+' • Tamamlanma: %'+qp+' • Deneme: '+(data.trial||0)); $('#questionProg').style.width=qp+'%';
}
function renderReport(){
  let box=$('#smartReport'); if(!box) return;
  let top=Object.entries(data.subjects||{}).sort((a,b)=>(b[1].min||0)-(a[1].min||0))[0];
  let openHw=data.homeworks.filter(h=>!h.done).length;
  box.innerHTML='<div class="subject-row"><strong>Genel Özet</strong><p class="muted">'+reportText()+'</p></div><div class="subject-row"><strong>Öncelik</strong><p class="muted">'+(openHw?'Açık ödevleri azalt.':'Açık ödev yok, yeni çalışma hedefi belirle.')+'</p></div><div class="subject-row"><strong>Ders Odağı</strong><p class="muted">'+(top?'Bu hafta '+top[0]+' iyi gidiyor.':'Henüz ders verisi oluşmadı.')+'</p></div>';
}
function renderMonthly(){
  let box=$('#monthlyReport'); if(!box) return;
  let m=monthKey();
  let monthSessions=data.sessions.filter(s=>(s.date||'').slice(0,7)===m);
  let min=monthSessions.reduce((a,s)=>a+(s.min||0),0);
  let monthHw=data.homeworks.filter(h=>(h.created||'').slice(0,7)===m);
  let doneHw=monthHw.filter(h=>h.done).length;
  let bySubject={}; monthSessions.forEach(s=>bySubject[s.subject]=(bySubject[s.subject]||0)+(s.min||0));
  let top=Object.entries(bySubject).sort((a,b)=>b[1]-a[1])[0];
  box.innerHTML='<div class="subject-row"><strong>Bu Ay</strong><p class="muted">'+min+' dk • '+monthSessions.length+' seans • '+doneHw+'/'+monthHw.length+' ödev</p></div><div class="subject-row"><strong>Ayın Dersi</strong><p class="muted">'+(top?top[0]+' • '+top[1]+' dk':'Henüz aylık ders verisi yok.')+'</p></div><div class="subject-row"><strong>Hatırlatma</strong><p class="muted">'+(data.reminder.on==='on'?'Aktif: '+data.reminder.time+' • '+data.reminder.note:'Kapalı')+'</p></div>';
}
function defaultExamDate(k){return {YKS:'2026-06-20',TYT:'2026-06-20',AYT:'2026-06-21',YDT:'2026-06-21',AGS:'2026-07-26',DGS:'2026-07-19',ALES_2:'2026-08-02',KPSS:'2026-09-06',KPSS_ALAN_1:'2026-09-12',KPSS_ALAN_2:'2026-09-13',YDS_2:'2026-11-08',LGS:'2026-06-13'}[k]||'2026-06-20'}
function defaultExamTime(k){return {YKS:'10:15',TYT:'10:15',AYT:'10:15',YDT:'15:45',AGS:'10:15',DGS:'10:15',ALES_2:'10:15',KPSS:'10:15',KPSS_ALAN_1:'10:15',KPSS_ALAN_2:'10:15',YDS_2:'10:15',LGS:'09:30'}[k]||'10:15'}
function renderExam(){
  let ex=$('#exam'), dt=$('#examDate'); if(ex) ex.value=data.exam||'YKS'; if(dt) dt.value=data.examDate||defaultExamDate(data.exam||'YKS');
  setText('examChip',data.exam||'YKS'); let k=data.exam||'YKS'; let target=new Date((data.examDate||defaultExamDate(k))+'T'+defaultExamTime(k)+':00');
  let diff=target-new Date(), s=Math.max(0,Math.floor(diff/1000)); let d=Math.floor(s/86400); s-=d*86400; let h=Math.floor(s/3600); s-=h*3600; let m=Math.floor(s/60); s-=m*60;
  setText('d',days(d)); setText('h',String(h).padStart(2,'0')); setText('m',String(m).padStart(2,'0')); setText('s',String(s).padStart(2,'0')); setText('examNote',diff>0?'Sınav tarih ve saatine göre kalan süre hesaplanıyor.':'Seçilen sınav tarihi geçti veya bugün.');
}
function exportCsv(){
  let rows=['type,date,subject,title_or_minutes,topic,status,timestamp'];
  data.sessions.forEach(s=>rows.push(['session',s.date,s.subject,s.min,'','',new Date(s.ts||Date.now()).toISOString()].join(',')));
  data.homeworks.forEach(h=>rows.push(['homework',h.created||'',h.subject,h.title,h.topic||'',h.done?'done':'open',h.due||''].join(',')));
  download('sezr-focus-rapor.csv',rows.join('\n'),'text/csv');
}
function exportJson(){download('sezr-focus-yedek-'+today()+'.json',JSON.stringify(data,null,2),'application/json')}
function importJson(file){let r=new FileReader(); r.onload=()=>{try{let obj=JSON.parse(r.result); if(!obj||typeof obj!=='object') throw Error(); data=obj; init(); save(); render(); alert('Yedek içe aktarıldı.')}catch(e){alert('Geçersiz yedek dosyası.')}}; r.readAsText(file)}
function complete(){let min=Math.round(total/60), sub=$('#subjectSelect')?.value||'Genel'; data.min=(data.min||0)+min; data.pom=(data.pom||0)+1; data.subjects[sub]=data.subjects[sub]||{min:0,pom:0}; data.subjects[sub].min+=min; data.subjects[sub].pom+=1; data.sessions.push({date:today(),subject:sub,min,ts:Date.now()}); save()}
function tick(){if(!run)return; remain--; if(remain<=0){run=false; clearInterval(timer); complete(); remain=total; setText('toggle','Başlat'); setText('status','Tamamlandı')} render()}
function bind(){
  $('#toggle').onclick=()=>{run=!run; setText('toggle',run?'Duraklat':'Başlat'); setText('status',run?'Çalışıyor':'Duraklatıldı'); clearInterval(timer); if(run) timer=setInterval(tick,1000)};
  $('#reset').onclick=()=>{run=false; clearInterval(timer); remain=total; render()};
  $$('.modes button').forEach(b=>b.onclick=()=>{total=Number(b.dataset.min)*60; remain=total; render()});
  $('#add').onclick=()=>{let v=$('#task').value.trim(); if(v){data.tasks.push({text:v,done:false,created:today()}); $('#task').value=''; save(); render()}};
  $('#addHw').onclick=()=>{let title=$('#hwTitle').value.trim(), topic=$('#hwTopic').value.trim(), subject=$('#hwSubject').value||'Genel', due=$('#hwDue').value; if(title){data.homeworks.push({title,topic,subject,due,done:false,created:today()}); $('#hwTitle').value=''; $('#hwTopic').value=''; save(); render()}};
  $('#studentName').onchange=e=>{data.profile.name=e.target.value; save(); render()}; $('#studentClass').onchange=e=>{data.profile.className=e.target.value; save(); render()}; $('#studentGoal').onchange=e=>{data.profile.goal=e.target.value; save(); render()};
  $('#reminderTime').onchange=e=>{data.reminder.time=e.target.value; save(); render()}; $('#reminderNote').onchange=e=>{data.reminder.note=e.target.value; save(); render()}; $('#reminderOn').onchange=e=>{data.reminder.on=e.target.value; save(); render()};
  $('#copyReport').onclick=()=>{if(navigator.clipboard) navigator.clipboard.writeText(reportText()); alert('Rapor kopyalandı.')};
  $('#dailyGoal').onchange=e=>{data.goal=Number(e.target.value)||120; save(); render()}; $('#themeMode').onchange=e=>{data.theme=e.target.value; save(); render()};
  $('#exam').onchange=e=>{data.exam=e.target.value; data.examDate=defaultExamDate(data.exam); save(); render()}; $('#examDate').onchange=e=>{data.examDate=e.target.value; save(); render()};
  $('#questionGoal').onchange=e=>{data.qGoal=Number(e.target.value)||0; save(); render()}; $('#questionDone').onchange=e=>{data.qDone=Number(e.target.value)||0; save(); render()}; $('#trialCount').onchange=e=>{data.trial=Number(e.target.value)||0; save(); render()};
  $('#exportCsv').onclick=exportCsv; $('#exportJson').onclick=exportJson; $('#importJsonBtn').onclick=()=>$('#importJson').click(); $('#importJson').onchange=e=>{if(e.target.files&&e.target.files[0]) importJson(e.target.files[0])};
  $('#resetData').onclick=()=>{if(confirm('Tüm Focus verileri silinsin mi?')){localStorage.removeItem('focus'); location.reload()}}; $('#full').onclick=()=>$('#overlay').classList.add('show'); $('#oclose').onclick=()=>$('#overlay').classList.remove('show');
}
init(); bind(); setInterval(renderExam,1000); render();
