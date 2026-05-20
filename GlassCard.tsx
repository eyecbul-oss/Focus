import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
export const firebaseConfig={apiKey:'BURAYA_FIREBASE_API_KEY',authDomain:'BURAYA_AUTH_DOMAIN',projectId:'BURAYA_PROJECT_ID'};
const app=initializeApp(firebaseConfig);
export const auth=getAuth(app);
export const db=getFirestore(app);
