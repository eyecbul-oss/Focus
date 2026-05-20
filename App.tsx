import React from 'react';
import { StatusBar } from 'expo-status-bar';
import RootNavigator from './app/navigation/RootNavigator';
import useAppBootstrap from './app/hooks/useAppBootstrap';

export default function App() {
  useAppBootstrap();

  return (
    <>
      <RootNavigator />
      <StatusBar style="light" />
    </>
  );
}
