(function(){
  function $(q){return document.querySelector(q)}
  function today(){return new Date().toISOString().slice(0,10)}
  function getData(){try{return JSON.parse(localStorage.getItem('focus')||'{}')}catch(e){return {}}}
  function saveData(data){localStorage.setItem('focus',JSON.stringify(data))}
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
    var data=ensure(getData());
    var total=(data.sessions||[]).reduce(function(a,s){return a+sessionMinutes(s)},0);
    var last=(data.sessions||[]).slice(-1)[0];
    setText('pomodoroText','Toplam '+total+' dk çalışma kaydı var.'+(last?' Son oturum: '+(last.subject||'Genel')+' '+sessionMinutes(last)+' dk.':''));
    var net=Number(data.yks.trialNet||0), target=Number(data.yks.targetNet||80), diff=Math.max(0,target-net);
    var yks=$('#yksText');
    if(yks){yks.textContent+=' • Son net: '+net+' • Hedefe kalan: '+diff+' net'+(data.yks.strongTopic?' • Güçlü konu: '+data.yks.strongTopic:'')}
    renderHistory();
  }
  function bindUpgrades(){
    var data=ensure(getData());
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
