import { createContext, useContext, useEffect, useState } from 'react';
import { AuthUser } from '../types/auth';
import { mockUsers } from '../data/mockData';

interface AuthContextType {
  user: { id: string; email: string } | null;
  authUser: AuthUser | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const AUTH_STORAGE_KEY = 'auth_user_id';

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<{ id: string; email: string } | null>(null);
  const [authUser, setAuthUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUserId = localStorage.getItem(AUTH_STORAGE_KEY);
    if (storedUserId) {
      const foundUser = mockUsers.find(u => u.id === storedUserId);
      if (foundUser) {
        setUser({ id: foundUser.id, email: foundUser.email });
        setAuthUser({
          profile: foundUser.profile,
          role: foundUser.role,
        });
      }
    }
    setLoading(false);
  }, []);

  const signIn = async (email: string, password: string) => {
    return new Promise<void>((resolve, reject) => {
      setTimeout(() => {
        const foundUser = mockUsers.find(
          u => u.email === email && u.password === password
        );

        if (!foundUser) {
          reject(new Error('Email ou mot de passe incorrect'));
          return;
        }

        const userAuth = { id: foundUser.id, email: foundUser.email };
        setUser(userAuth);
        setAuthUser({
          profile: foundUser.profile,
          role: foundUser.role,
        });

        localStorage.setItem(AUTH_STORAGE_KEY, foundUser.id);
        resolve();
      }, 500);
    });
  };

  const signOut = async () => {
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        setUser(null);
        setAuthUser(null);
        localStorage.removeItem(AUTH_STORAGE_KEY);
        resolve();
      }, 300);
    });
  };

  return (
    <AuthContext.Provider value={{ user, authUser, loading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
