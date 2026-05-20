import { useEffect, useRef, useState } from 'react';
export default function useFocusTimer(initialSeconds:number){
  const [remaining,setRemaining]=useState(initialSeconds);
  const [running,setRunning]=useState(false);
  const ref=useRef<any>(null);
  useEffect(()=>{setRemaining(initialSeconds);setRunning(false)},[initialSeconds]);
  useEffect(()=>{if(!running)return;ref.current=setInterval(()=>setRemaining(r=>{if(r<=1){setRunning(false);return 0}return r-1}),1000);return()=>clearInterval(ref.current)},[running]);
  return {remaining,running,start:()=>setRunning(true),pause:()=>setRunning(false),reset:()=>{setRunning(false);setRemaining(initialSeconds)}};
}
