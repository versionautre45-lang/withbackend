import { useState } from 'react';
import { LogIn, Building2 } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { signIn } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await signIn(email, password);
    } catch (err: any) {
      setError(err.message || 'Erreur de connexion');
    } finally {
      setLoading(false);
    }
  };

  const demoAccounts = [
    {
      role: 'Responsable RH',
      email: 'rh@company.com',
      password: 'password123',
      color: 'from-orange-500 to-red-500',
    },
    {
      role: 'Encadreur',
      email: 'encadreur@company.com',
      password: 'password123',
      color: 'from-green-500 to-emerald-600',
    },
    {
      role: 'Stagiaire',
      email: 'stagiaire@company.com',
      password: 'password123',
      color: 'from-blue-500 to-cyan-600',
    },
  ];

  const quickLogin = (demoEmail: string, demoPassword: string) => {
    setEmail(demoEmail);
    setPassword(demoPassword);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 to-red-50 dark:from-gray-900 dark:to-gray-800 p-4">
      <div className="w-full max-w-6xl grid md:grid-cols-2 gap-8">
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-xl p-8 border border-gray-100 dark:border-gray-800">
          <div className="flex items-center gap-3 mb-8">
            <div className="p-3 bg-gradient-to-br from-orange-500 to-red-500 rounded-xl shadow-lg">
              <Building2 className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
                Système de Gestion
              </h1>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Portail de connexion
              </p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Adresse email
              </label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                placeholder="utilisateur@company.com"
                required
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Mot de passe
              </label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                placeholder="••••••••"
                required
              />
            </div>

            {error && (
              <div className="p-3 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800">
                <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 text-white rounded-lg font-medium transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-xl"
            >
              <LogIn className="w-5 h-5" />
              {loading ? 'Connexion...' : 'Se connecter'}
            </button>
          </form>
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
            Comptes de démonstration
          </h2>
          <p className="text-gray-600 dark:text-gray-400 mb-6">
            Cliquez sur un compte pour remplir automatiquement les identifiants
          </p>

          {demoAccounts.map((account, index) => (
            <button
              key={index}
              onClick={() => quickLogin(account.email, account.password)}
              className="w-full text-left p-6 bg-white dark:bg-gray-900 rounded-xl shadow-lg border border-gray-100 dark:border-gray-800 hover:shadow-xl transition-all group"
            >
              <div className="flex items-center gap-4">
                <div className={`p-3 bg-gradient-to-br ${account.color} rounded-lg shadow-md`}>
                  <LogIn className="w-6 h-6 text-white" />
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-orange-600 dark:group-hover:text-orange-400 transition-colors">
                    {account.role}
                  </h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {account.email}
                  </p>
                </div>
              </div>
              <div className="mt-4 pt-4 border-t border-gray-100 dark:border-gray-800">
                <p className="text-xs text-gray-500 dark:text-gray-500">
                  Mot de passe: <span className="font-mono bg-gray-50 dark:bg-gray-800 px-2 py-1 rounded">
                    {account.password}
                  </span>
                </p>
              </div>
            </button>
          ))}

          <div className="mt-8 p-6 bg-orange-50 dark:bg-orange-900/20 rounded-xl border border-orange-200 dark:border-orange-800">
            <h3 className="font-semibold text-orange-900 dark:text-orange-100 mb-2">
              Hiérarchie des rôles
            </h3>
            <ul className="text-sm text-orange-800 dark:text-orange-200 space-y-2">
              <li>• <strong>Responsable RH:</strong> Crée les comptes encadreurs, voit tout</li>
              <li>• <strong>Encadreur:</strong> Crée les comptes stagiaires, gère ses stagiaires</li>
              <li>• <strong>Stagiaire:</strong> Voit ses projets et tâches assignés</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
