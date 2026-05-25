import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { View, Text } from 'react-native';
import { COLORS } from '../theme/colors';
import TimerScreen from '../screens/TimerScreen';
import StatsScreen from '../screens/StatsScreen';

const Tab = createBottomTabNavigator();

function Placeholder({ title }: { title: string }) {
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
      <Tab.Screen name='Koç'>{() => <Placeholder title='Koç' />}</Tab.Screen>
      <Tab.Screen name='Görev'>{() => <Placeholder title='Görevler' />}</Tab.Screen>
      <Tab.Screen name='Sayaç' component={TimerScreen} />
      <Tab.Screen name='İstatistik' component={StatsScreen} />
      <Tab.Screen name='Ritim'>{() => <Placeholder title='Ritim' />}</Tab.Screen>
    </Tab.Navigator>
  );
}
