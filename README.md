import React, { useState } from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import { COLORS } from '../../theme/colors';
import { useAuthStore } from '../../store/authStore';
import { loginWithEmail, registerWithEmail } from '../../services/firebaseAuthService';

export default function AuthScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [registerMode, setRegisterMode] = useState(false);
  const [message, setMessage] = useState('');
  const login = useAuthStore((s) => s.login);
  const continueGuest = useAuthStore((s) => s.continueGuest);
  const setCloudStatus = useAuthStore((s) => s.setCloudStatus);

  const submit = async () => {
    try {
      setMessage(registerMode ? 'Hesap oluşturuluyor...' : 'Giriş yapılıyor...');
      const user = registerMode
        ? await registerWithEmail(email.trim(), password)
        : await loginWithEmail(email.trim(), password);
      login(user.email || email.trim());
      setCloudStatus('Bulut senkron aktif');
      setMessage('Başarılı');
    } catch (e) {
      setMessage('Giriş yapılamadı. Firebase ayarlarını veya şifreni kontrol et.');
    }
  };

  return (
    <GradientBackground>
      <View style={styles.container}>
        <GlassCard>
          <Text style={styles.brand}>SezR <Text style={{ color: COLORS.primary }}>Focus</Text></Text>
          <Text style={styles.title}>{registerMode ? 'Yeni hesap oluştur' : 'Hesabına giriş yap'}</Text>
          <Text style={styles.info}>Mail ve şifre ile cihazlar arası senkron kullanabilirsin.</Text>

          <TextInput placeholder="mail@adres.com" placeholderTextColor={COLORS.muted} value={email} onChangeText={setEmail} autoCapitalize="none" style={styles.input} />
          <TextInput placeholder="Şifre" placeholderTextColor={COLORS.muted} value={password} onChangeText={setPassword} secureTextEntry style={styles.input} />

          <NeonButton title={registerMode ? 'Hesap Oluştur' : 'Giriş Yap'} onPress={submit} />
          <NeonButton title={registerMode ? 'Giriş ekranına dön' : 'Yeni hesap oluştur'} variant="dark" onPress={() => setRegisterMode(!registerMode)} />
          <NeonButton title="Misafir Devam Et" variant="dark" onPress={continueGuest} />

          {!!message && <Text style={styles.message}>{message}</Text>}
        </GlassCard>
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 18 },
  brand: { color: COLORS.text, fontSize: 34, fontWeight: '900', marginBottom: 14 },
  title: { color: COLORS.text, fontSize: 24, fontWeight: '900' },
  info: { color: COLORS.muted, fontWeight: '800', lineHeight: 22, marginVertical: 14 },
  input: { minHeight: 52, borderRadius: 18, backgroundColor: 'rgba(2,6,23,.50)', color: COLORS.text, paddingHorizontal: 14, fontWeight: '800', borderWidth: 1, borderColor: COLORS.border, marginBottom: 12 },
  message: { color: COLORS.primary, fontWeight: '900', marginTop: 12 },
});
