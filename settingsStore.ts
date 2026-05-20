import { createUserWithEmailAndPassword, signInWithEmailAndPassword, signOut } from 'firebase/auth';
import { auth } from './firebase';

export async function registerWithEmail(email: string, password: string) {
  if (password.length < 6) throw new Error('Şifre en az 6 karakter olmalı.');
  const result = await createUserWithEmailAndPassword(auth, email, password);
  return result.user;
}

export async function loginWithEmail(email: string, password: string) {
  const result = await signInWithEmailAndPassword(auth, email, password);
  return result.user;
}

export async function logoutFirebase() {
  await signOut(auth);
}

export function getCurrentUser() {
  return auth.currentUser;
}
