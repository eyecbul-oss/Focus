import { create } from 'zustand';

type FocusState = {
  totalMinutes: number;
  sessions: number;
  addSession: (minutes: number) => void;
  hydrate: (data: Partial<FocusState>) => void;
  snapshot: () => { totalMinutes: number; sessions: number };
};

export const useFocusStore = create<FocusState>((set, get) => ({
  totalMinutes: 0,
  sessions: 0,

  addSession: (minutes) =>
    set((state) => ({
      totalMinutes: state.totalMinutes + minutes,
      sessions: state.sessions + 1,
    })),

  hydrate: (data) =>
    set({
      totalMinutes: data.totalMinutes ?? 0,
      sessions: data.sessions ?? 0,
    }),

  snapshot: () => ({
    totalMinutes: get().totalMinutes,
    sessions: get().sessions,
  }),
}));
