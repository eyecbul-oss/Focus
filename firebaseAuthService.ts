// 🔐 Firebase Kimlik Doğrulama Servisi - Focus Uygulaması
import { auth } from './firebase';
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  User,
} from 'firebase/auth';

export type AuthUser = {
  uid: string;
  email: string;
};

/**
 * Email ve şifre ile kullanıcı kaydı oluştur
 */
export async function registerWithEmail(email: string, password: string): Promise<AuthUser> {
  if (!email || !password) {
    throw new Error('Email ve şifre gerekli');
  }

  if (password.length < 6) {
    throw new Error('Şifre en az 6 karakter olmalı');
  }

  try {
    const result = await createUserWithEmailAndPassword(auth, email, password);
    return {
      uid: result.user.uid,
      email: result.user.email || email,
    };
  } catch (error: any) {
    if (error.code === 'auth/email-already-in-use') {
      throw new Error('Bu email adı zaten kullanılıyor');
    } else if (error.code === 'auth/invalid-email') {
      throw new Error('Geçersiz email formatı');
    } else if (error.code === 'auth/weak-password') {
      throw new Error('Şifre çok zayıf');
    }
    throw new Error(error.message || 'Hesap oluşturulamadı');
  }
}

/**
 * Email ve şifre ile giriş yap
 */
export async function loginWithEmail(email: string, password: string): Promise<AuthUser> {
  if (!email || !password) {
    throw new Error('Email ve şifre gerekli');
  }

  try {
    const result = await signInWithEmailAndPassword(auth, email, password);
    return {
      uid: result.user.uid,
      email: result.user.email || email,
    };
  } catch (error: any) {
    if (error.code === 'auth/user-not-found') {
      throw new Error('Kullanıcı bulunamadı');
    } else if (error.code === 'auth/wrong-password') {
      throw new Error('Şifre yanlış');
    } else if (error.code === 'auth/invalid-email') {
      throw new Error('Geçersiz email formatı');
    } else if (error.code === 'auth/too-many-requests') {
      throw new Error('Çok fazla başarısız deneme. Lütfen sonra tekrar deneyin.');
    }
    throw new Error(error.message || 'Giriş yapılamadı');
  }
}

/**
 * Çıkış yap
 */
export async function logout(): Promise<void> {
  try {
    await signOut(auth);
  } catch (error: any) {
    throw new Error(error.message || 'Çıkış yapılamadı');
  }
}

/**
 * Mevcut kullanıcı bilgilerini al
 */
export function getCurrentUser(): AuthUser | null {
  const user = auth.currentUser;
  if (!user) return null;
  return {
    uid: user.uid,
    email: user.email || 'unknown@example.com',
  };
}
