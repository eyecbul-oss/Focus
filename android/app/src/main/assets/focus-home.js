function focusHomeSetText(id,text){const el=document.getElementById(id);if(el)el.textContent=text}
function focusHomeRender(){
  try{
    const store=JSON.parse(localStorage.getItem('focus')||'{}');
    const min=Number(store.min||0), goal=Number(store.goal||120), q=Number(store.qDone||0);
    const left=Math.max(0,goal-min), pct=goal?Math.min(100,Math.round(min/goal*100)):0;
    const trials=store.trials||{}, now=new Date();
    let trialWeek=0;
    for(let i=6;i>=0;i--){const d=new Date(now);d.setDate(now.getDate()-i);trialWeek+=Number(trials[d.toISOString().slice(0,10)]||0)}
    focusHomeSetText('homeGoalLeft',left);
    focusHomeSetText('homeQuestion',q);
    focusHomeSetText('homeTrial',trialWeek);
    focusHomeSetText('homeExam',store.exam||'YKS');
    focusHomeSetText('homeFocusChip','%'+pct);
    let advice='Bugün hedefe başlamak için kısa bir odak seansı seç.';
    if(pct>=100)advice='Bugünkü dakika hedefi tamamlandı. İstersen soru veya deneme hedefini artır.';
    else if(left<=30)advice='Hedefe çok az kaldı. 25 dakikalık kısa bir seans yeterli olabilir.';
    else if(q===0)advice='Bugün henüz soru girişi yok. YKS sekmesinden soru hedefini güncelle.';
    focusHomeSetText('homeAdvice',advice);
    focusHomeSetText('homeStatusText','Bugün '+min+'/'+goal+' dk çalışıldı • Hedefe kalan: '+left+' dk • Soru: '+q+' • Haftalık deneme: '+trialWeek);
  }catch(e){}
}
function focusHomeSwitchStudy(){
  if(typeof switchScreen==='function')switchScreen('study');
  else document.querySelectorAll('.tab-btn[data-screen="study"]').forEach(b=>b.click());
}
function focusHomeQuickStart(subject,minutes){
  const select=document.getElementById('subjectSelect');
  if(select){
    const has=[...select.options].some(o=>o.value===subject);
    if(!has){const o=document.createElement('option');o.value=subject;o.textContent=subject;select.appendChild(o)}
    select.value=subject;
  }
  const min=Number(minutes||25);
  if(typeof total!=='undefined'){total=min*60;remain=total;run=false;if(timer)clearInterval(timer)}
  document.querySelectorAll('.modes button').forEach(b=>b.classList.toggle('active',Number(b.dataset.min)===min));
  const status=document.getElementById('status');if(status)status.textContent=subject+' • '+min+' dk hazır';
  const toggle=document.getElementById('toggle');if(toggle)toggle.textContent='Başlat';
  focusHomeSwitchStudy();
  if(typeof render==='function')render();
}
function focusHomeBind(){
  document.querySelectorAll('.quick-start').forEach(btn=>{
    btn.addEventListener('click',()=>focusHomeQuickStart(btn.dataset.subject||'Genel',btn.dataset.min||25));
  });
  focusHomeRender();
  setInterval(focusHomeRender,2500);
}
if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',focusHomeBind);else focusHomeBind();
