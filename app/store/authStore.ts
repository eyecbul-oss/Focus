import { create } from 'zustand';

type AuthState = {
  email: string | null;
  uid: string | null;
  guest: boolean;
  cloudStatus: string;
  loginGuest: () => void;
  loginEmail: (email: string) => void;
  logout: () => void;
  setCloudStatus: (status: string) => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  email: null,
  uid: null,
  guest: false,
  cloudStatus: 'Hazır',

  loginGuest: () =>
    set({
      guest: true,
      email: null,
      uid: 'guest',
    }),

  loginEmail: (email) =>
    set({
      email,
      uid: email.replace(/[^a-zA-Z0-9]/g, '_'),
      guest: false,
    }),

  logout: () =>
    set({
      email: null,
      uid: null,
      guest: false,
    }),

  setCloudStatus: (cloudStatus) => set({ cloudStatus }),
}));
