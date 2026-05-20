
function focusTaskInput(){
document.getElementById('taskInput').focus();
}

function selectMode(el){
document.querySelectorAll('.mode').forEach(m=>{
m.classList.remove('active');
});
el.classList.add('active');
}

let total=0;
let completed=0;

function addTask(){

const input=document.getElementById('taskInput');

if(input.value.trim()==='') return;

const li=document.createElement('li');

li.innerHTML=`
<input type="checkbox" onchange="toggleTask(this)">
<span>${input.value}</span>
`;

document.getElementById('taskList').appendChild(li);

total++;

updateStats();

input.value='';
}

function toggleTask(el){

if(el.checked){
completed++;
}else{
completed--;
}

updateStats();
}

function updateStats(){

document.getElementById('taskCounter').innerText=
completed + ' / ' + total;

const percent= total===0 ? 0 :
Math.round((completed/total)*100);

document.getElementById('progressText').innerText=
`Görev ilerlemesi: %${percent} (${completed}/${total})`;

document.getElementById('statRate').innerText=
percent + '%';
}
