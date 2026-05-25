import { create } from 'zustand';

export type Task = {
  id: string;
  title: string;
  done: boolean;
};

export type Exam = {
  id: string;
  name: string;
  date: string;
};

type FocusSnapshot = {
  totalMinutes: number;
  sessions: number;
  streak: number;
  lastSessionDate: string | null;
  tasks: Task[];
  exams: Exam[];
};

type FocusState = FocusSnapshot & {
  addSession: (minutes: number) => void;
  addTask: (title: string) => void;
  toggleTask: (id: string) => void;
  removeTask: (id: string) => void;
  addExam: (name: string, date: string) => void;
  removeExam: (id: string) => void;
  hydrate: (data: Partial<FocusSnapshot>) => void;
  snapshot: () => FocusSnapshot;
};

function todayKey() {
  return new Date().toISOString().slice(0, 10);
}

function nextStreak(current: number, lastDate: string | null) {
  const today = todayKey();
  if (lastDate === today) return current;

  if (!lastDate) return 1;

  const last = new Date(lastDate);
  const diff = Math.round((new Date(today).getTime() - last.getTime()) / 86400000);
  return diff === 1 ? current + 1 : 1;
}

export const useFocusStore = create<FocusState>((set, get) => ({
  totalMinutes: 0,
  sessions: 0,
  streak: 0,
  lastSessionDate: null,
  tasks: [],
  exams: [],

  addSession: (minutes) =>
    set((state) => ({
      totalMinutes: state.totalMinutes + minutes,
      sessions: state.sessions + 1,
      streak: nextStreak(state.streak, state.lastSessionDate),
      lastSessionDate: todayKey(),
    })),

  addTask: (title) =>
    set((state) => ({
      tasks: [
        { id: Date.now().toString(), title: title.trim(), done: false },
        ...state.tasks,
      ].filter((task) => task.title.length > 0),
    })),

  toggleTask: (id) =>
    set((state) => ({
      tasks: state.tasks.map((task) =>
        task.id === id ? { ...task, done: !task.done } : task,
      ),
    })),

  removeTask: (id) =>
    set((state) => ({
      tasks: state.tasks.filter((task) => task.id !== id),
    })),

  addExam: (name, date) =>
    set((state) => ({
      exams: [
        { id: Date.now().toString(), name: name.trim(), date: date.trim() },
        ...state.exams,
      ].filter((exam) => exam.name.length > 0),
    })),

  removeExam: (id) =>
    set((state) => ({
      exams: state.exams.filter((exam) => exam.id !== id),
    })),

  hydrate: (data) =>
    set({
      totalMinutes: data.totalMinutes ?? 0,
      sessions: data.sessions ?? 0,
      streak: data.streak ?? 0,
      lastSessionDate: data.lastSessionDate ?? null,
      tasks: data.tasks ?? [],
      exams: data.exams ?? [],
    }),

  snapshot: () => ({
    totalMinutes: get().totalMinutes,
    sessions: get().sessions,
    streak: get().streak,
    lastSessionDate: get().lastSessionDate,
    tasks: get().tasks,
    exams: get().exams,
  }),
}));
