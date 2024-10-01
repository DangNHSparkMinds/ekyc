import { Navigate, Route, Routes } from 'react-router-dom';
import './App.css';
import { ConfigProvider, theme } from 'antd';
import MainLayout from './components/Layouts/MainLayout';
import Login from './features/Account/Login';
import RegisterRouter from './features/Register/RegisterRouter';
import KycRouter from './features/Kyc/KycRouter';
import Dashboard from './features/Dashboard';

function App() {
  const StartRedirect = () => <Navigate to="/login" replace />;

  return (
    <ConfigProvider theme={{ algorithm: theme.defaultAlgorithm }}>
      <Routes>
        <Route path='/' element={<StartRedirect />} />
        <Route path='/login' element={<Login />} />
        <Route path="/register/*" element={<RegisterRouter />} />
        <Route element={<MainLayout />}>
          <Route path='/dashboard' element={<Dashboard />} />
          <Route path="/kyc/*" element={<KycRouter />} />
        </Route>
      </Routes>
    </ConfigProvider>
  );
}

export default App;
