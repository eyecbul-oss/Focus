var FOCUS_EXAMS={
YKS:{date:'2026-06-20',time:'10:15',name:'YKS'},
TYT:{date:'2026-06-20',time:'10:15',name:'TYT'},
AYT:{date:'2026-06-21',time:'10:15',name:'AYT'},
YDT:{date:'2026-06-21',time:'15:45',name:'YDT'},
AGS:{date:'2026-07-12',time:'10:15',name:'MEB-AGS'},
DGS:{date:'2026-07-19',time:'10:15',name:'DGS'},
ALES_2:{date:'2026-11-23',time:'10:15',name:'ALES/2'},
KPSS:{date:'2026-09-06',time:'10:15',name:'KPSS Lisans'},
KPSS_ALAN_1:{date:'2026-09-12',time:'10:15',name:'KPSS Alan Bilgisi 1. Gün'},
KPSS_ALAN_2:{date:'2026-09-13',time:'10:15',name:'KPSS Alan Bilgisi 2. Gün'},
YDS_2:{date:'2026-11-08',time:'10:15',name:'YDS/2'},
LGS:{date:'2026-06-13',time:'09:30',name:'LGS'}
};
function focusExamApply(){try{
var oldDate=window.defaultExamDate,oldTime=window.defaultExamTime;
window.defaultExamDate=function(k){return FOCUS_EXAMS[k]?FOCUS_EXAMS[k].date:(oldDate?oldDate(k):'2026-06-20')};
window.defaultExamTime=function(k){return FOCUS_EXAMS[k]?FOCUS_EXAMS[k].time:(oldTime?oldTime(k):'10:15')};
if(window.data&&data.exam&&FOCUS_EXAMS[data.exam]&&(!data.examDate||data.examDate==='2026-06-14')){data.examDate=FOCUS_EXAMS[data.exam].date;if(typeof save==='function')save()}
if(typeof renderExam==='function')renderExam();
}catch(e){}}
if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',focusExamApply);else focusExamApply();
