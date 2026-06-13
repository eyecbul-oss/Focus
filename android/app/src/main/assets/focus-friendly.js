function focusFriendlySetText(id,text){var el=document.getElementById(id);if(el)el.textContent=text}
function focusFriendlyEmpty(id,text){var el=document.getElementById(id);if(!el)return;if(!el.children.length&&!el.textContent.trim()){el.innerHTML='<div class="empty-state">'+text+'</div>'}}
function focusFriendlyLevel(){try{var chip=document.getElementById('levelChip');var title=document.getElementById('levelTitle');var n=Number((document.getElementById('levelNo')||{}).textContent||1);var names=['Başlangıç','Düzenli Çalışan','İstikrarlı','Disiplinli','Odak Ustası','Efsanevi Seri'];var name=names[Math.min(names.length-1,Math.max(0,n-1))];if(title)title.textContent=name;if(chip)chip.textContent='Seviye '+n}catch(e){}}
function focusFriendlyRender(){
focusFriendlyLevel();
focusFriendlyEmpty('tasks','📝 Henüz görev eklemedin. İlk görevini ekleyerek başla.');
focusFriendlyEmpty('homeworks','📚 Henüz ödev eklenmedi. Çalışacağın konuyu ekleyebilirsin.');
focusFriendlyEmpty('badges','🏆 İlk rozetini kazanmak için çalışmaya başla.');
var week=document.getElementById('weekText');if(week&&week.textContent.trim()==='Haftalık toplam: 0 dk')week.textContent='Bu hafta henüz çalışma kaydı bulunmuyor.';
var goal=document.getElementById('homeStatusText');if(goal&&goal.textContent.trim()==='Bugünkü hedefin hazırlanıyor.')goal.textContent='Bugün henüz çalışma yapılmadı. İlk Pomodoro seansını başlatabilirsin.';
}
function focusFriendlyBind(){focusFriendlyRender();setInterval(focusFriendlyRender,1800)}
if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',focusFriendlyBind);else focusFriendlyBind();
