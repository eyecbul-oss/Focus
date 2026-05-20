import { create } from 'zustand';
export type TaskPriority='critical'|'normal'|'light';
export type FocusTask={id:string;title:string;type:string;priority:TaskPriority;done:boolean};
type FocusState={tasks:FocusTask[];focusSeconds:number;dailyTarget:number;totalToday:number;addTask:(title:string,type?:string,priority?:TaskPriority)=>void;toggleTask:(id:string)=>void;setFocusSeconds:(s:number)=>void;addTodaySeconds:(s:number)=>void};
export const useFocusStore=create<FocusState>((set)=>({
  tasks:[{id:'1',title:'Problemler 20 soru',type:'Soru',priority:'critical',done:false},{id:'2',title:'Paragraf 15 soru',type:'Paragraf',priority:'normal',done:false}],
  focusSeconds:25*60,dailyTarget:60,totalToday:0,
  addTask:(title,type='Soru',priority='normal')=>set(s=>({tasks:[{id:Date.now().toString(),title,type,priority,done:false},...s.tasks]})),
  toggleTask:(id)=>set(s=>({tasks:s.tasks.map(t=>t.id===id?{...t,done:!t.done}:t)})),
  setFocusSeconds:(focusSeconds)=>set({focusSeconds}),
  addTodaySeconds:(seconds)=>set(s=>({totalToday:s.totalToday+seconds}))
}));
