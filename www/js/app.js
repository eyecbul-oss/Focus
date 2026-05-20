const state = {
  modeName: localStorage.getItem('modeName') || 'Standart',
  modeMinutes: Number(localStorage.getItem('modeMinutes') || 25),
  tasks: JSON.parse(localStorage.getItem('tasks') || '[]'),
  todayMinutes: Number(localStorage.getItem('todayMinutes') || 0),
  streak: Number(localStorage.getItem('streak') || 0),
  timerLeft: 25 * 60,
  timerTotal: 25 * 60,
  timerRunning: false,
  timerId: null
};

const $ = (id) => document.getElementById(id);

function save(){
  localStorage.setItem('tasks', JSON.stringify(state.tasks));
  localStorage.setItem('todayMinutes', String(state.todayMinutes));
  localStorage.setItem('streak', String(state.streak));
  localStorage.setItem('modeName', state.modeName);
  localStorage.setItem('modeMinutes', String(state.modeMinutes));
}

function focusTaskInput(){
  $('tasksSection').scrollIntoView({behavior:'smooth'});
  setTimeout(() => $('taskInput').focus(), 450);
}

function scrollToTasks(){
  $('tasksSection').scrollIntoView({behavior:'smooth'});
}

function showToast(message){
  const toast = $('toast');
  toast.textContent = message;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 1700);
}

function selectMode(el){
  document.querySelectorAll('.mode').forEach(m => m.classList.remove('active'));
  el.classList.add('active');

  state.modeName = el.dataset.name;
  state.modeMinutes = Number(el.dataset.min);
  state.timerTotal = state.modeMinutes * 60;
  state.timerLeft = state.timerTotal;
  state.timerRunning = false;
  clearInterval(state.timerId);

  $('taskMode').value = state.modeName;
  save();
  renderTimer();
  renderStats();
}

function addTask(){
  const text = $('taskInput').value.trim();
  if(!text){
    showToast('Önce küçük bir görev yaz');
    return;
  }

  state.tasks.unshift({
    id: Date.now(),
    text,
    category: $('category').value,
    level: $('level').value,
    mode: $('taskMode').value,
    done: false
  });

  $('taskInput').value = '';
  save();
  renderTasks();
  showToast('Görev eklendi');
}

function quickTask(){
  $('taskInput').value = '10 dakikalık hızlı tekrar';
  $('taskMode').value = 'Quick Start';
  focusTaskInput();
}

function toggleTask(id){
  const task = state.tasks.find(t => t.id === id);
  if(task){
    task.done = !task.done;
    if(task.done && state.streak === 0) state.streak = 1;
    save();
    renderTasks();
  }
}

function removeTask(id){
  state.tasks = state.tasks.filter(t => t.id !== id);
  save();
  renderTasks();
}

function clearCompleted(){
  state.tasks = state.tasks.filter(t => !t.done);
  save();
  renderTasks();
}

function clearAll(){
  if(state.tasks.length === 0) return;
  state.tasks = [];
  save();
  renderTasks();
  showToast('Tüm görevler temizlendi');
}

function renderTasks(){
  const list = $('taskList');
  list.innerHTML = '';

  state.tasks.forEach(task => {
    const li = document.createElement('li');
    li.className = task.done ? 'done' : '';
    li.innerHTML = `
      <input type="checkbox" ${task.done ? 'checked' : ''} onchange="toggleTask(${task.id})">
      <span>
        ${escapeHtml(task.text)}
        <small class="task-meta">${task.category} · ${task.level} · ${task.mode}</small>
      </span>
      <button class="remove-btn" onclick="removeTask(${task.id})">Sil</button>
    `;
    list.appendChild(li);
  });

  $('emptyState').style.display = state.tasks.length ? 'none' : 'block';
  renderStats();
}

function renderStats(){
  const total = state.tasks.length;
  const done = state.tasks.filter(t => t.done).length;
  const percent = total ? Math.round((done / total) * 100) : 0;

  $('taskCounter').textContent = `${done} / ${total}`;
  $('statRate').textContent = `${percent}%`;
  $('statMinutes').textContent = `${state.todayMinutes} dk`;
  $('statStreak').textContent = String(state.streak);
  $('progressText').textContent = `Görev ilerlemesi: %${percent} (${done}/${total})`;
  $('progressFill').style.width = `${percent}%`;
  $('selectedModePill').textContent = `${state.modeName} · ${state.modeMinutes} dk`;
}

function startFocus(){
  if(state.timerRunning){
    pauseTimer();
    return;
  }

  state.timerRunning = true;
  showToast('Odak başladı');

  state.timerId = setInterval(() => {
    state.timerLeft--;

    if(state.timerLeft <= 0){
      clearInterval(state.timerId);
      state.timerRunning = false;
      state.todayMinutes += state.modeMinutes;
      state.timerLeft = state.timerTotal;
      if(state.streak === 0) state.streak = 1;
      save();
      renderStats();
      showToast('Odak seansı tamamlandı');
    }

    renderTimer();
  }, 1000);
}

function pauseTimer(){
  state.timerRunning = false;
  clearInterval(state.timerId);
  showToast('Odak duraklatıldı');
}

function resetTimer(){
  pauseTimer();
  state.timerTotal = state.modeMinutes * 60;
  state.timerLeft = state.timerTotal;
  renderTimer();
}

function renderTimer(){
  const min = Math.floor(state.timerLeft / 60).toString().padStart(2,'0');
  const sec = (state.timerLeft % 60).toString().padStart(2,'0');
  $('timerText').textContent = `${min}:${sec}`;
  $('timerLabel').textContent = state.timerRunning ? 'Odak çalışıyor' : `${state.modeName} mod`;
  const doneRatio = 1 - (state.timerLeft / state.timerTotal);
  document.documentElement.style.setProperty('--progress', `${Math.max(0, doneRatio) * 360}deg`);
}

function escapeHtml(str){
  return str.replace(/[&<>"']/g, s => ({
    '&':'&amp;',
    '<':'&lt;',
    '>':'&gt;',
    '"':'&quot;',
    "'":'&#039;'
  }[s]));
}

function init(){
  state.timerTotal = state.modeMinutes * 60;
  state.timerLeft = state.timerTotal;

  document.querySelectorAll('.mode').forEach(m => {
    if(m.dataset.name === state.modeName) m.classList.add('active');
    else m.classList.remove('active');
  });

  $('taskMode').value = state.modeName;
  renderTimer();
  renderTasks();

  $('taskInput').addEventListener('keydown', (e) => {
    if(e.key === 'Enter') addTask();
  });
}

init();
