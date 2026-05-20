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

type FocusSnapshot = {
  tasks: FocusTask[];
  notes: FocusNote[];
  focusSeconds: number;
  dailyTarget: number;
  totalToday: number;
  pomodoros: number;
  breakSeconds: number;
};

type FocusState = FocusSnapshot & {
  addTask: (title: string, type?: string, priority?: TaskPriority) => void;
  toggleTask: (id: string) => void;
  addNote: (text: string) => void;
  noteToTask: (id: string) => void;
  setFocusSeconds: (seconds: number) => void;
  setBreakSeconds: (seconds: number) => void;
  addTodaySeconds: (seconds: number) => void;
  finishPomodoro: () => void;
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
  focusSeconds: 25 * 60,
  dailyTarget: 60,
  totalToday: 0,
  pomodoros: 0,
  breakSeconds: 5 * 60,

  addTask: (title, type = 'Soru', priority = 'normal') =>
    set((s) => ({ tasks: [{ id: Date.now().toString(), title, type, priority, done: false }, ...s.tasks] })),

  toggleTask: (id) =>
    set((s) => ({ tasks: s.tasks.map((t) => (t.id === id ? { ...t, done: !t.done } : t)) })),

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
  addTodaySeconds: (seconds) => set((s) => ({ totalToday: s.totalToday + seconds })),
  finishPomodoro: () => set((s) => ({ pomodoros: s.pomodoros + 1 })),

  hydrate: (data) => set((s) => ({ ...s, ...data })),

  snapshot: () => {
    const s = get();
    return {
      tasks: s.tasks,
      notes: s.notes,
      focusSeconds: s.focusSeconds,
      dailyTarget: s.dailyTarget,
      totalToday: s.totalToday,
      pomodoros: s.pomodoros,
      breakSeconds: s.breakSeconds,
    };
  },
}));
