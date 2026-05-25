import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { View, Text } from 'react-native';
import { COLORS } from '../theme/colors';

const Tab = createBottomTabNavigator();

function Screen({ title }: { title: string }) {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: COLORS.bg }}>
      <Text style={{ color: COLORS.text, fontSize: 24 }}>{title}</Text>
    </View>
  );
}

export default function BottomTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: COLORS.cardSolid,
          borderTopColor: COLORS.border,
        },
        tabBarActiveTintColor: COLORS.primary,
        tabBarInactiveTintColor: COLORS.muted,
      }}
    >
      <Tab.Screen name='Koç'>{() => <Screen title='Koç' />}</Tab.Screen>
      <Tab.Screen name='Görev'>{() => <Screen title='Görevler' />}</Tab.Screen>
      <Tab.Screen name='Sayaç'>{() => <Screen title='Focus Sayaç' />}</Tab.Screen>
      <Tab.Screen name='Sınav'>{() => <Screen title='Sınav' />}</Tab.Screen>
      <Tab.Screen name='Ritim'>{() => <Screen title='Ritim' />}</Tab.Screen>
    </Tab.Navigator>
  );
}
