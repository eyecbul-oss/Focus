import { create } from 'zustand';
import { signInAnonymously } from 'firebase/auth';
import { auth } from '../services/firebase';

type AuthState = {
  email: string | null;
  uid: string | null;
  guest: boolean;
  cloudStatus: string;
  loginGuest: () => Promise<void>;
  loginEmail: (email: string) => void;
  logout: () => void;
  setCloudStatus: (status: string) => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  email: null,
  uid: null,
  guest: false,
  cloudStatus: 'Hazır',

  loginGuest: async () => {
    const userCredential = await signInAnonymously(auth);

    set({
      guest: true,
      email: null,
      uid: userCredential.user.uid,
      cloudStatus: 'Misafir bulut aktif',
    });
  },

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
