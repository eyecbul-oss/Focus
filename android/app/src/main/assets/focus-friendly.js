function focusFriendlySetText(id,text){var el=document.getElementById(id);if(el)el.textContent=text}
function focusFriendlyEmpty(id,text){var el=document.getElementById(id);if(!el)return;if(!el.children.length&&!el.textContent.trim()){el.innerHTML='<div class="empty-state">'+text+'</div>'}}
function focusFriendlyLevel(){try{var chip=document.getElementById('levelChip');var title=document.getElementById('levelTitle');var n=Number((document.getElementById('levelNo')||{}).textContent||1);var names=['Başlangıç','Düzenli Çalışan','İstikrarlı','Disiplinli','Odak Ustası','Efsanevi Seri'];var name=names[Math.min(names.length-1,Math.max(0,n-1))];if(title)title.textContent=name;if(chip)chip.textContent='Seviye '+n}catch(e){}}
function focusFriendlyHumanize(){
var task=document.getElementById('taskText');if(task&&task.textContent.indexOf('Görev ilerlemesi')===0)task.textContent=task.textContent.replace('Görev ilerlemesi','Yapılacaklar');
var hw=document.getElementById('hwText');if(hw&&hw.textContent.indexOf('Ödev ilerlemesi')===0)hw.textContent=hw.textContent.replace('Ödev ilerlemesi','Ödev durumu');
var xp=document.getElementById('xpText');if(xp)xp.textContent='Puan; çalışma süresi, tamamlanan görevler ve Pomodoro seanslarından hesaplanır.';
var level=document.getElementById('levelText');if(level&&level.textContent.indexOf('Bir sonraki')>-1)level.textContent='Çalışmaya devam ettikçe seviyen yükselir.';
}
function focusFriendlyRender(){
focusFriendlyLevel();
focusFriendlyHumanize();
focusFriendlyEmpty('tasks','📝 Henüz görev eklemedin. İlk görevini ekleyerek başla.');
focusFriendlyEmpty('homeworks','📚 Henüz ödev eklenmedi. Çalışacağın konuyu ekleyebilirsin.');
focusFriendlyEmpty('badges','🏆 İlk rozetini kazanmak için çalışmaya başla.');
var week=document.getElementById('weekText');if(week&&week.textContent.trim()==='Haftalık toplam: 0 dk')week.textContent='Bu hafta henüz çalışma kaydı bulunmuyor.';
var goal=document.getElementById('homeStatusText');if(goal&&goal.textContent.trim()==='Bugünkü hedefin hazırlanıyor.')goal.textContent='Bugün henüz çalışma yapılmadı. İlk Pomodoro seansını başlatabilirsin.';
}
function focusFriendlyBind(){focusFriendlyRender();setInterval(focusFriendlyRender,1800)}
if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',focusFriendlyBind);else focusFriendlyBind();
