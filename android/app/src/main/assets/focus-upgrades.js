(function(){
  var FOCUS_EXAMS={YKS:{date:'2026-06-20',time:'10:15',name:'YKS'},TYT:{date:'2026-06-20',time:'10:15',name:'TYT'},AYT:{date:'2026-06-21',time:'10:15',name:'AYT'},YDT:{date:'2026-06-21',time:'15:45',name:'YDT'},AGS:{date:'2026-07-12',time:'10:15',name:'MEB-AGS'},DGS:{date:'2026-07-19',time:'10:15',name:'DGS'},ALES_2:{date:'2026-11-23',time:'10:15',name:'ALES/2'},KPSS:{date:'2026-09-06',time:'10:15',name:'KPSS Lisans'},KPSS_ALAN_1:{date:'2026-09-12',time:'10:15',name:'KPSS Alan Bilgisi 1. Gün'},KPSS_ALAN_2:{date:'2026-09-13',time:'10:15',name:'KPSS Alan Bilgisi 2. Gün'},YDS_2:{date:'2026-11-08',time:'10:15',name:'YDS/2'},LGS:{date:'2026-06-13',time:'09:30',name:'LGS'}};
  function $(q){return document.querySelector(q)}
  function today(){return new Date().toISOString().slice(0,10)}
  function getData(){try{return JSON.parse(localStorage.getItem('focus')||'{}')}catch(e){return {}}}
  function saveData(data){localStorage.setItem('focus',JSON.stringify(data))}
  function applyExamCalendar(){try{var oldDate=window.defaultExamDate,oldTime=window.defaultExamTime;window.defaultExamDate=function(k){return FOCUS_EXAMS[k]?FOCUS_EXAMS[k].date:(oldDate?oldDate(k):'2026-06-20')};window.defaultExamTime=function(k){return FOCUS_EXAMS[k]?FOCUS_EXAMS[k].time:(oldTime?oldTime(k):'10:15')};var d=getData();if(d.exam&&FOCUS_EXAMS[d.exam]&&(!d.examDate||d.examDate==='2026-06-14')){d.examDate=FOCUS_EXAMS[d.exam].date;saveData(d)}if(typeof renderExam==='function')renderExam()}catch(e){}}
  function ensure(data){
    data.sessions=data.sessions||[];
    data.homeworks=data.homeworks||[];
    data.tasks=data.tasks||[];
    data.trials=data.trials||{};
    data.settings=data.settings||{};
    data.settings.sessionGoal=data.settings.sessionGoal||25;
    data.settings.shortBreak=data.settings.shortBreak||5;
    data.settings.longBreak=data.settings.longBreak||15;
    data.yks=data.yks||{};
    data.yks.trialNet=data.yks.trialNet||0;
    data.yks.targetNet=data.yks.targetNet||80;
    data.yks.strongTopic=data.yks.strongTopic||'';
    data.students=data.students||[];
    data.activeStudentId=data.activeStudentId||'default';
    return data;
  }
  function setValue(id,value){var el=$('#'+id);if(el)el.value=value}
  function setText(id,text){var el=$('#'+id);if(el)el.textContent=text}
  function sessionMinutes(s){return Number(s.min||s.minutes||0)}
  function renderHistory(){
    var box=$('#sessionHistory');
    if(!box)return;
    var data=ensure(getData());
    var list=(data.sessions||[]).slice(-8).reverse();
    if(!list.length){box.innerHTML='<p class="muted">Henüz oturum geçmişi yok.</p>';return}
    box.innerHTML='';
    list.forEach(function(s){
      var row=document.createElement('div');
      row.className='subject-row';
      row.innerHTML='<strong>'+(s.subject||'Genel')+'</strong><p class="muted">'+(s.date||today())+' • '+sessionMinutes(s)+' dk</p>';
      box.appendChild(row);
    });
  }
  function renderUpgradeTexts(){
    applyExamCalendar();
    var data=ensure(getData());
    var total=(data.sessions||[]).reduce(function(a,s){return a+sessionMinutes(s)},0);
    var last=(data.sessions||[]).slice(-1)[0];
    setText('pomodoroText','Toplam '+total+' dk çalışma kaydı var.'+(last?' Son oturum: '+(last.subject||'Genel')+' '+sessionMinutes(last)+' dk.':''));
    var net=Number(data.yks.trialNet||0), target=Number(data.yks.targetNet||80), diff=Math.max(0,target-net);
    var yks=$('#yksText');
    if(yks&&yks.textContent.indexOf('Son net')<0){yks.textContent+=' • Son net: '+net+' • Hedefe kalan: '+diff+' net'+(data.yks.strongTopic?' • Güçlü konu: '+data.yks.strongTopic:'')}
    renderHistory();
  }
  function bindUpgrades(){
    applyExamCalendar();
    var data=ensure(getData());saveData(data);
    setValue('sessionGoal',data.settings.sessionGoal);
    setValue('shortBreak',data.settings.shortBreak);
    setValue('longBreak',data.settings.longBreak);
    setValue('trialNet',data.yks.trialNet);
    setValue('targetNet',data.yks.targetNet);
    setValue('strongTopic',data.yks.strongTopic);
    var sg=$('#sessionGoal'); if(sg)sg.onchange=function(e){var d=ensure(getData());d.settings.sessionGoal=Number(e.target.value)||25;saveData(d);renderUpgradeTexts()};
    var sb=$('#shortBreak'); if(sb)sb.onchange=function(e){var d=ensure(getData());d.settings.shortBreak=Number(e.target.value)||5;saveData(d);renderUpgradeTexts()};
    var lb=$('#longBreak'); if(lb)lb.onchange=function(e){var d=ensure(getData());d.settings.longBreak=Number(e.target.value)||15;saveData(d);renderUpgradeTexts()};
    var tn=$('#trialNet'); if(tn)tn.onchange=function(e){var d=ensure(getData());d.yks.trialNet=Number(e.target.value)||0;saveData(d);renderUpgradeTexts()};
    var tg=$('#targetNet'); if(tg)tg.onchange=function(e){var d=ensure(getData());d.yks.targetNet=Number(e.target.value)||80;saveData(d);renderUpgradeTexts()};
    var st=$('#strongTopic'); if(st)st.onchange=function(e){var d=ensure(getData());d.yks.strongTopic=e.target.value||'';saveData(d);renderUpgradeTexts()};
    var clear=$('#clearHistory'); if(clear)clear.onclick=function(){if(confirm('Oturum geçmişi temizlensin mi?')){var d=ensure(getData());d.sessions=[];saveData(d);renderHistory();renderUpgradeTexts()}}
  }
  window.addEventListener('load',function(){setTimeout(function(){bindUpgrades();renderUpgradeTexts()},300)});
})();