import React from 'react';
import { NavigationContainer, DefaultTheme } from '@react-navigation/native';
import BottomTabs from './BottomTabs';
import { COLORS } from '../theme/colors';

const theme = {...DefaultTheme, colors:{...DefaultTheme.colors, background:COLORS.bg, text:COLORS.text, primary:COLORS.primary, card:COLORS.bg, border:'transparent'}};

export default function RootNavigator(){ return <NavigationContainer theme={theme}><BottomTabs/></NavigationContainer>; }
