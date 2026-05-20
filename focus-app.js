const $ = (id) => document.getElementById(id);
const todayKey = () => new Date().toISOString().slice(0,10);
const LS = "sezr_focus_site_premium_v1";

let state = {
  date: todayKey(),
  tasks: [],
  notes: [],
  totalSeconds: 0,
  focusSeconds: 1500,
  remaining: 1500,
  running: false,
  mode: "Standart",
  breakRemaining: 300,
  breakRunning: false,
  exam: { label: "TYT", date: "2026-06-20" },
  streak: 0
};

let timerId = null;
let breakId = null;

function load(){
  try{
    const raw = localStorage.getItem(LS);
    if(raw) state = { ...state, ...JSON.parse(raw) };
  }catch(e){}
  if(state.date !== todayKey()){
    state.date = todayKey();
    state.totalSeconds = 0;
    state.tasks = state.tasks.filter(t => !t.done);
    state.streak = state.streak || 0;
  }
  if(!state.remaining) state.remaining = state.focusSeconds;
}

function save(){
  localStorage.setItem(LS, JSON.stringify(state));
  const el = $("saveStatus");
  if(el) el.textContent = "Kayıtlar bu cihazda saklanıyor. Son kayıt: " + new Date().toLocaleTimeString("tr-TR");
}

function fmt(sec){
  sec = Math.max(0, Math.floor(sec));
  const m = Math.floor(sec/60);
  const s = sec % 60;
  return `${String(m).padStart(2,"0")}:${String(s).padStart(2,"0")}`;
}

function taskStats(){
  const total = state.tasks.length;
  const done = state.tasks.filter(t=>t.done).length;
  const pct = total ? Math.round(done/total*100) : 0;
  return { total, done, pct };
}

function score(){
  const minutes = Math.floor(state.totalSeconds/60);
  const ts = taskStats();
  return Math.min(100, Math.round(minutes * 0.8 + ts.pct * 0.6));
}

function daysLeft(date){
  const diff = new Date(date + "T10:00:00").getTime() - Date.now();
  return Math.max(0, Math.floor(diff / 86400000));
}

function coachText(){
  const ts = taskStats();
  const min = Math.floor(state.totalSeconds/60);
  if(ts.total === 0) return ["Bugünkü ilk görevini ekle.", "Küçük ve net bir görev yaz. Sonra 10 dakikalık Quick Start ile başla."];
  if(ts.done === ts.total) return ["Bugünkü görevler tamamlandı.", "Yeni konu yerine kısa tekrar veya yanlış analizi daha verimli olabilir."];
  if(min === 0) return ["Görevlerin hazır.", "İlk görevi seç ve kısa bir odak seansı başlat."];
  return ["Ritmi koruyorsun.", "Hedefe yaklaşmak için bir kısa seans daha yeterli olabilir."];
}

function render(){
  $("timerText").textContent = fmt(state.remaining);
  $("fsTimer").textContent = fmt(state.remaining);
  $("timerMode").textContent = state.mode;
  $("todayMinutes").textContent = Math.floor(state.totalSeconds/60) + " dk";
  const ts = taskStats();
  $("taskRate").textContent = "%" + ts.pct;
  $("focusScore").textContent = score();
  const [title, text] = coachText();
  $("coachTitle").textContent = title;
  $("coachText").textContent = text;
  $("smartSuggestion").textContent = ts.total === 0 ? "Önce küçük bir görev ekle." : ts.done === ts.total ? "Bugün görevlerin tamam. Kısa tekrar yapabilirsin." : "Kalan görevlere sırayla odaklan.";
  $("miniGoal").textContent = state.totalSeconds === 0 ? "10 dakika + 1 görev." : "Bugün " + Math.floor(state.totalSeconds/60) + " dk tamamlandı.";
  $("rhythmText").textContent = state.streak > 0 ? state.streak + " günlük seri aktif." : "İlk seansla ritim başlar.";
  document.querySelector(".timer-ring").style.setProperty("--progress", `${100 - (state.remaining/state.focusSeconds*100)}%`);
  renderTasks();
  renderNotes();
  renderExam();
  save();
}

function renderTasks(){
  const list = $("taskList");
  list.innerHTML = "";
  state.tasks
    .slice()
    .sort((a,b)=>({critical:0,normal:1,light:2}[a.priority]-({critical:0,normal:1,light:2}[b.priority]))
    .forEach(task=>{
      const div = document.createElement("div");
      div.className = "task-item " + (task.done ? "done " : "") + task.priority;
      div.innerHTML = `
        <div class="check">${task.done ? "✓" : ""}</div>
        <div class="task-body">
          <div class="task-title"></div>
          <div class="pills">
            <span class="pill">Görev</span>
            <span class="pill ${task.priority}">${task.priority === "critical" ? "Kritik" : task.priority === "light" ? "Hafif" : "Normal"}</span>
          </div>
        </div>
        <button class="delete-btn">Sil</button>
      `;
      div.querySelector(".task-title").textContent = task.title;
      div.querySelector(".check").onclick = () => { task.done = !task.done; render(); };
      div.querySelector(".task-title").onclick = () => { task.done = !task.done; render(); };
      div.querySelector(".delete-btn").onclick = () => {
        if(confirm("Bu görev silinsin mi?")){
          state.tasks = state.tasks.filter(t=>t.id !== task.id);
          render();
        }
      };
      list.appendChild(div);
    });
}

function renderNotes(){
  const list = $("noteList");
  list.innerHTML = "";
  state.notes.forEach(note=>{
    const div = document.createElement("div");
    div.className = "note-item";
    div.innerHTML = `
      <div class="note-body">
        <p></p>
        <span>${new Date(note.createdAt).toLocaleString("tr-TR")}</span>
      </div>
      <button class="soft-btn">Görev Yap</button>
    `;
    div.querySelector("p").textContent = note.text;
    div.querySelector("button").onclick = () => {
      state.tasks.unshift({ id: Date.now().toString(), title: "Nottan görev: " + note.text, done:false, priority:"normal" });
      render();
    };
    list.appendChild(div);
  });
}

function renderExam(){
  const d = daysLeft(state.exam.date);
  $("examDays").textContent = d;
  $("examLabel").textContent = state.exam.label + " için gün kaldı";
  $("examAdvice").textContent = d <= 30 ? "Deneme analizi ve eksik kapatma daha önemli." : "Konu eksiklerini kapat ve kısa soru pratiği yap.";
}

function tick(){
  if(!state.running) return;
  state.remaining--;
  state.totalSeconds++;
  if(state.remaining <= 0){
    state.running = false;
    clearInterval(timerId);
    state.remaining = 0;
    state.streak = Math.max(1, state.streak || 1);
    alert("Focus seansı tamamlandı.");
  }
  render();
}

function startPause(){
  state.running = !state.running;
  $("startPauseBtn").textContent = state.running ? "Duraklat" : "Başlat";
  $("fsToggleBtn").textContent = state.running ? "Duraklat" : "Başlat";
  if(state.running){
    clearInterval(timerId);
    timerId = setInterval(tick,1000);
  }else{
    clearInterval(timerId);
  }
  render();
}

function setMode(btn){
  document.querySelectorAll(".mode-card").forEach(b=>b.classList.remove("active"));
  btn.classList.add("active");
  state.focusSeconds = Number(btn.dataset.seconds);
  state.remaining = state.focusSeconds;
  state.mode = btn.querySelector("b").textContent;
  state.running = false;
  clearInterval(timerId);
  $("startPauseBtn").textContent = "Başlat";
  render();
}

function openBreak(){
  state.running = false;
  clearInterval(timerId);
  $("startPauseBtn").textContent = "Başlat";
  $("breakModal").classList.remove("hidden");
  renderBreak();
}

function renderBreak(){
  $("breakText").textContent = fmt(state.breakRemaining);
}

function breakTick(){
  if(!state.breakRunning) return;
  state.breakRemaining--;
  if(state.breakRemaining <= 0){
    state.breakRunning = false;
    clearInterval(breakId);
    alert("Mola bitti.");
  }
  renderBreak();
}

function startBreak(){
  state.breakRunning = !state.breakRunning;
  $("breakStartBtn").textContent = state.breakRunning ? "Molayı Duraklat" : "Mola Başlat";
  if(state.breakRunning){
    clearInterval(breakId);
    breakId = setInterval(breakTick,1000);
  }else clearInterval(breakId);
}

function endBreak(){
  state.breakRunning = false;
  clearInterval(breakId);
  state.breakRemaining = 300;
  $("breakModal").classList.add("hidden");
  renderBreak();
}

function bind(){
  $("startPauseBtn").onclick = startPause;
  $("fsToggleBtn").onclick = startPause;
  $("resetBtn").onclick = () => { state.running=false; clearInterval(timerId); state.remaining=state.focusSeconds; $("startPauseBtn").textContent="Başlat"; render(); };
  $("quickStartBtn").onclick = () => setMode(document.querySelector('[data-mode="quick"]'));
  $("deepBtn").onclick = () => setMode(document.querySelector('[data-mode="deep"]'));
  $("breakBtn").onclick = openBreak;
  $("breakStartBtn").onclick = startBreak;
  $("breakEndBtn").onclick = endBreak;
  $("fullscreenBtn").onclick = () => $("fullscreenView").classList.remove("hidden");
  $("fsExitBtn").onclick = () => $("fullscreenView").classList.add("hidden");
  $("settingsBtn").onclick = () => $("settingsPanel").classList.toggle("show");
  $("closeSettingsBtn").onclick = () => $("settingsPanel").classList.remove("show");
  $("exportBtn").onclick = () => {
    const blob = new Blob([JSON.stringify(state,null,2)], {type:"application/json"});
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "sezr-focus-kayit.json";
    a.click();
  };

  $("addTaskBtn").onclick = () => {
    const title = $("taskInput").value.trim();
    if(!title) return;
    state.tasks.unshift({ id:Date.now().toString(), title, done:false, priority:$("taskPriority").value });
    $("taskInput").value = "";
    render();
  };
  $("taskInput").addEventListener("keydown", e=>{ if(e.key==="Enter") $("addTaskBtn").click(); });
  $("clearDoneBtn").onclick = () => {
    if(confirm("Tamamlanan görevler silinsin mi?")){
      state.tasks = state.tasks.filter(t=>!t.done);
      render();
    }
  };

  $("addNoteBtn").onclick = () => {
    const text = $("noteInput").value.trim();
    if(!text) return;
    state.notes.unshift({ id:Date.now().toString(), text, createdAt:new Date().toISOString() });
    $("noteInput").value = "";
    render();
  };

  document.querySelectorAll(".mode-card").forEach(btn=>btn.onclick = () => setMode(btn));
  document.querySelectorAll(".exam-btn").forEach(btn=>btn.onclick = () => {
    document.querySelectorAll(".exam-btn").forEach(b=>b.classList.remove("active"));
    btn.classList.add("active");
    state.exam = { label: btn.dataset.exam, date: btn.dataset.date };
    render();
  });

  document.addEventListener("click", (e)=>{
    if(!$("settingsPanel").contains(e.target) && !$("settingsBtn").contains(e.target)){
      $("settingsPanel").classList.remove("show");
    }
  });
}

load();
bind();
render();
