import { create } from 'zustand';

export type TaskPriority = 'critical' | 'normal' | 'light';

export type FocusTask = {
  id: string;
  title: string;
  type: string;
  priority: TaskPriority;
  done: boolean;
};

export type FocusNote = {
  id: string;
  text: string;
  createdAt: string;
};

export type FocusHistoryItem = {
  id: string;
  minutes: number;
  mode: string;
  createdAt: string;
};

export type ExamSettings = {
  type: 'TYT' | 'AYT' | 'LGS' | 'KPSS' | 'DGS' | 'Özel';
  date: string;
};

type FocusSnapshot = {
  tasks: FocusTask[];
  notes: FocusNote[];
  history: FocusHistoryItem[];
  exam: ExamSettings;
  focusSeconds: number;
  dailyTarget: number;
  totalToday: number;
  pomodoros: number;
  breakSeconds: number;
};

type FocusState = FocusSnapshot & {
  addTask: (title: string, type?: string, priority?: TaskPriority) => void;
  toggleTask: (id: string) => void;
  deleteTask: (id: string) => void;
  clearDoneTasks: () => void;
  addNote: (text: string) => void;
  noteToTask: (id: string) => void;
  setFocusSeconds: (seconds: number) => void;
  setBreakSeconds: (seconds: number) => void;
  setExam: (exam: ExamSettings) => void;
  addTodaySeconds: (seconds: number) => void;
  finishPomodoro: (minutes?: number, mode?: string) => void;
  hydrate: (data: Partial<FocusSnapshot>) => void;
  snapshot: () => FocusSnapshot;
};

const initialTasks: FocusTask[] = [
  { id: '1', title: 'Problemler 20 soru', type: 'Soru', priority: 'critical', done: false },
  { id: '2', title: 'Paragraf 15 soru', type: 'Paragraf', priority: 'normal', done: false },
];

export const useFocusStore = create<FocusState>((set, get) => ({
  tasks: initialTasks,
  notes: [],
  history: [],
  exam: { type: 'TYT', date: '2026-06-20' },
  focusSeconds: 25 * 60,
  dailyTarget: 60,
  totalToday: 0,
  pomodoros: 0,
  breakSeconds: 5 * 60,

  addTask: (title, type = 'Soru', priority = 'normal') =>
    set((s) => ({ tasks: [{ id: Date.now().toString(), title, type, priority, done: false }, ...s.tasks] })),

  toggleTask: (id) =>
    set((s) => ({ tasks: s.tasks.map((t) => (t.id === id ? { ...t, done: !t.done } : t)) })),

  deleteTask: (id) =>
    set((s) => ({ tasks: s.tasks.filter((t) => t.id !== id) })),

  clearDoneTasks: () =>
    set((s) => ({ tasks: s.tasks.filter((t) => !t.done) })),

  addNote: (text) =>
    set((s) => ({ notes: [{ id: Date.now().toString(), text, createdAt: new Date().toISOString() }, ...s.notes] })),

  noteToTask: (id) =>
    set((s) => {
      const note = s.notes.find((n) => n.id === id);
      if (!note) return s;
      return {
        tasks: [{ id: Date.now().toString(), title: 'Nottan görev: ' + note.text, type: 'Yanlış', priority: 'normal', done: false }, ...s.tasks],
      };
    }),

  setFocusSeconds: (focusSeconds) => set({ focusSeconds }),
  setBreakSeconds: (breakSeconds) => set({ breakSeconds }),
  setExam: (exam) => set({ exam }),
  addTodaySeconds: (seconds) => set((s) => ({ totalToday: s.totalToday + seconds })),

  finishPomodoro: (minutes = 25, mode = 'Standart') =>
    set((s) => ({
      pomodoros: s.pomodoros + 1,
      history: [{ id: Date.now().toString(), minutes, mode, createdAt: new Date().toISOString() }, ...s.history].slice(0, 80),
    })),

  hydrate: (data) => set((s) => ({ ...s, ...data })),

  snapshot: () => {
    const s = get();
    return {
      tasks: s.tasks,
      notes: s.notes,
      history: s.history,
      exam: s.exam,
      focusSeconds: s.focusSeconds,
      dailyTarget: s.dailyTarget,
      totalToday: s.totalToday,
      pomodoros: s.pomodoros,
      breakSeconds: s.breakSeconds,
    };
  },
}));
