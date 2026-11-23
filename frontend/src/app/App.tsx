import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ToastProvider } from "../components/ui/Toast";
import { AuthProvider } from "../contexts/AuthContext";
import { ProtectedRoute } from "../components/auth/ProtectedRoute";
import { AppLayout } from "../components/layout/AppLayout";

import { LoginPage } from "../pages/auth/LoginPage";

import { VoluntariosOverviewPage } from "../pages/voluntarios/VoluntariosOverviewPage";
import { GerenciarVoluntariosPage } from "../pages/voluntarios/GerenciarVoluntariosPage";
import { CadastrarVoluntarioPage } from "../pages/voluntarios/CadastrarVoluntarioPage";

import { OverviewGeralPage } from "../pages/dashboards/OverviewGeralPage";
import { ConsultarDashboardPage } from "../pages/dashboards/ConsultarDashboardPage";

import { FormulariosPage } from "../pages/formularios/FormulariosPage";
import { CriarFormularioPage } from "../pages/formularios/CriarFormularioPage";
import { RespostasFormularioPage } from "../pages/formularios/RespostasFormularioPage";
import { EncerrarFormularioPage } from "../pages/formularios/EncerrarFormularioPage";

import { SobrePage } from "../pages/sobre/SobrePage";

export function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<ProtectedRoute />}>
              <Route element={<AppLayout />}>
                <Route path="/" element={<OverviewGeralPage />} />

                <Route
                  path="/voluntarios"
                  element={<VoluntariosOverviewPage />}
                />
                <Route
                  path="/voluntarios/gerenciar"
                  element={<GerenciarVoluntariosPage />}
                />
                <Route
                  path="/voluntarios/novo"
                  element={<CadastrarVoluntarioPage />}
                />

                <Route path="/dashboards" element={<OverviewGeralPage />} />
                <Route
                  path="/dashboards/resumo"
                  element={<OverviewGeralPage />}
                />
                <Route
                  path="/dashboards/consultar"
                  element={<ConsultarDashboardPage />}
                />

                <Route path="/formularios" element={<FormulariosPage />} />
                <Route
                  path="/formularios/novo"
                  element={<CriarFormularioPage />}
                />
                <Route
                  path="/formularios/respostas"
                  element={<RespostasFormularioPage />}
                />
                <Route
                  path="/formularios/encerrar"
                  element={<EncerrarFormularioPage />}
                />

                <Route path="/sobre" element={<SobrePage />} />

                <Route path="*" element={<Navigate to="/" replace />} />
              </Route>
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
