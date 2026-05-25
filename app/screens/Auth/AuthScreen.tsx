import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native';
import { useAuthStore } from '../../store/authStore';
import { COLORS } from '../../theme/colors';

export default function AuthScreen() {
  const [email, setEmail] = useState('');
  const loginGuest = useAuthStore((s) => s.loginGuest);
  const loginEmail = useAuthStore((s) => s.loginEmail);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>SezR Focus</Text>

      <TextInput
        placeholder='E-posta'
        placeholderTextColor={COLORS.muted}
        value={email}
        onChangeText={setEmail}
        style={styles.input}
      />

      <TouchableOpacity style={styles.button} onPress={() => loginEmail(email)}>
        <Text style={styles.buttonText}>Giriş Yap</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={loginGuest}>
        <Text style={styles.guest}>Misafir devam et</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.bg,
    justifyContent: 'center',
    padding: 24,
  },
  title: {
    color: COLORS.text,
    fontSize: 32,
    fontWeight: '700',
    marginBottom: 24,
  },
  input: {
    backgroundColor: COLORS.cardSolid,
    color: COLORS.text,
    padding: 16,
    borderRadius: 14,
    marginBottom: 16,
  },
  button: {
    backgroundColor: COLORS.primary,
    padding: 16,
    borderRadius: 14,
    marginBottom: 20,
  },
  buttonText: {
    textAlign: 'center',
    fontWeight: '700',
  },
  guest: {
    color: COLORS.muted,
    textAlign: 'center',
  },
});
