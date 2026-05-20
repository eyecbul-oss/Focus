import React from 'react';
import { NavigationContainer, DefaultTheme } from '@react-navigation/native';
import BottomTabs from './BottomTabs';
import AuthScreen from '../screens/Auth/AuthScreen';
import { COLORS } from '../theme/colors';
import { useAuthStore } from '../store/authStore';

const theme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    background: COLORS.bg,
    text: COLORS.text,
    primary: COLORS.primary,
    card: COLORS.bg,
    border: 'transparent',
  },
};

export default function RootNavigator() {
  const email = useAuthStore((s) => s.email);
  const guest = useAuthStore((s) => s.guest);

  return (
    <NavigationContainer theme={theme}>
      {email || guest ? <BottomTabs /> : <AuthScreen />}
    </NavigationContainer>
  );
}
