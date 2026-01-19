import React, { useState, useEffect, useCallback } from 'react';
import ChatAndNotification from './ChatAndNotification'; // Fără "/components/"
// --- Funcție Helper pentru a decodifica un JWT ---
function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
        atob(base64)
            .split('')
            .map(function (c) {
              return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            })
            .join('')
    );
    const decoded = JSON.parse(jsonPayload);

    return {
      username: decoded.sub,
      role: decoded.role,
      userId: decoded.userId,
    };
  } catch (e) {
    console.error("Failed to decode JWT:", e);
    return null;
  }
}

// --- (NOU) Componentă Modal reutilizabilă ---
const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;

  return (
      <div
          className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4"
          onClick={onClose}
      >
        <div
            className="bg-gray-800 rounded-lg shadow-xl w-full max-w-lg border border-gray-700 overflow-hidden"
            onClick={(e) => e.stopPropagation()} // Oprește închiderea la click pe conținut
        >
          <div className="flex justify-between items-center p-4 border-b border-gray-700">
            <h3 className="text-xl font-semibold text-cyan-400">{title}</h3>
            <button
                onClick={onClose}
                className="text-gray-400 hover:text-white text-2xl"
            >
              &times;
            </button>
          </div>
          <div className="p-6">
            {children}
          </div>
        </div>
      </div>
  );
};

const UserForm = ({ initialUser, onSubmit, onCancel, apiError }) => {
  const isEdit = !!initialUser?.id;

  const [formData, setFormData] = useState({
    id: initialUser?.id || null,
    name: initialUser?.name || '',
    username: initialUser?.username || '',
    address: initialUser?.address || '',
    age: initialUser?.age || 18,
    role: initialUser?.role || 'CLIENT',
    password: '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    // Convertim age în număr
    const dataToSubmit = { ...formData, age: parseInt(formData.age, 10) };
    await onSubmit(dataToSubmit);
    setLoading(false);
  };

  return (
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Full Name</label>
          <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Username</label>
          <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
          />
        </div>
        {/* Câmpul de parolă apare DOAR la creare */}
        {!isEdit && (
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-1">Password</label>
              <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
                  required={!isEdit} // Obligatoriu doar la creare
              />
            </div>
        )}
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Address</label>
          <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
          />
        </div>
        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-300 mb-1">Age</label>
            <input
                type="number"
                name="age"
                value={formData.age}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
                required
                min="18"
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-300 mb-1">Role</label>
            <select
                name="role"
                value={formData.role}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
            >
              <option value="CLIENT">CLIENT</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
        </div>
        {apiError && (
            <div className="p-3 bg-red-800 border border-red-600 text-red-100 rounded-lg text-sm">
              {apiError}
            </div>
        )}
        <div className="flex justify-end gap-4 pt-4">
          <button
              type="button"
              onClick={onCancel}
              className="py-2 px-4 rounded-lg font-semibold bg-gray-600 hover:bg-gray-500"
          >
            Cancel
          </button>
          <button
              type="submit"
              disabled={loading}
              className="py-2 px-4 rounded-lg font-semibold bg-cyan-600 hover:bg-cyan-500"
          >
            {loading ? 'Saving...' : (isEdit ? 'Update User' : 'Create User')}
          </button>
        </div>
      </form>
  );
};

// --- (NOU) Formular pentru Device-uri ---
const DeviceForm = ({ initialDevice, users, onSubmit, onCancel, apiError }) => {
  const [formData, setFormData] = useState({
    id: initialDevice?.id || null,
    name: initialDevice?.name || '',
    description: initialDevice?.description || '',
    address: initialDevice?.address || '',
    maxConsumption: initialDevice?.maxConsumption || 0,
    userId: initialDevice?.userId || '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const dataToSubmit = { ...formData, maxConsumption: parseFloat(formData.maxConsumption) };
    if (!dataToSubmit.userId) {
      alert("You must assign a user.");
      setLoading(false);
      return;
    }
    await onSubmit(dataToSubmit);
    setLoading(false);
  };

  const isEdit = !!formData.id;

  return (
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Device Name</label>
          <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Description</label>
          <input
              type="text"
              name="description"
              value={formData.description}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Address</label>
          <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Max Consumption (kWh)</label>
          <input
              type="number"
              name="maxConsumption"
              value={formData.maxConsumption}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
              min="0"
              step="0.1"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Assign to User</label>
          <select
              name="userId"
              value={formData.userId}
              onChange={handleChange}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg"
              required
          >
            <option value="">Select a user...</option>
            {users.map(user => (
                <option key={user.id} value={user.id}>
                  {user.username} (ID: {user.id.substring(0, 8)}...)
                </option>
            ))}
          </select>
        </div>

        {apiError && (
            <div className="p-3 bg-red-800 border border-red-600 text-red-100 rounded-lg text-sm">
              {apiError}
            </div>
        )}
        <div className="flex justify-end gap-4 pt-4">
          <button
              type="button"
              onClick={onCancel}
              className="py-2 px-4 rounded-lg font-semibold bg-gray-600 hover:bg-gray-500"
          >
            Cancel
          </button>
          <button
              type="submit"
              disabled={loading}
              className="py-2 px-4 rounded-lg font-semibold bg-cyan-600 hover:bg-cyan-500"
          >
            {loading ? 'Saving...' : (isEdit ? 'Update Device' : 'Create Device')}
          </button>
        </div>
      </form>
  );
};


// --- Componenta Pagină de Login (Neschimbată) ---
const LoginPage = ({ onLogin, error }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    await onLogin(username, password);
    setLoading(false);
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white p-4">
        <div className="w-full max-w-md p-8 bg-gray-800 rounded-lg shadow-xl border border-gray-700">
          <h2 className="text-3xl font-bold text-center text-cyan-400 mb-6">
            Energy Management System
          </h2>
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-300 mb-2">Username</label>
              <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  required
              />
            </div>
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-300 mb-2">Password</label>
              <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  required
              />
            </div>
            {error && (
                <div className="mb-4 p-3 bg-red-800 border border-red-600 text-red-100 rounded-lg text-center">
                  {error}
                </div>
            )}
            <button
                type="submit"
                disabled={loading}
                className={`w-full py-2 px-4 rounded-lg font-semibold text-white transition-all ${
                    loading
                        ? 'bg-gray-600 cursor-not-allowed'
                        : 'bg-cyan-600 hover:bg-cyan-500 shadow-lg shadow-cyan-600/30'
                }`}
            >
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>
        </div>
      </div>
  );
};

const ClientDashboard = ({ user, token, api }) => {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchClientDevices = useCallback(async () => {
    if (!user?.userId) return;

    setLoading(true);
    setError(null);
    try {
      const response = await api.get(`/devices/user/${user.userId}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch devices. Status: ${response.status}`);
      }
      const data = await response.json();
      setDevices(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [user, api]);

  useEffect(() => {
    fetchClientDevices();
  }, [fetchClientDevices]);

  return (
      <div className="p-8">
        <h1 className="text-3xl font-bold mb-6">Welcome, {user.username}! (Client)</h1>
        <h2 className="text-2xl font-semibold mb-4 text-cyan-400">Your Assigned Devices</h2>

        {loading && <p className="text-gray-300">Loading your devices...</p>}
        {error && <p className="text-red-400 bg-red-900 p-3 rounded-lg">{error}</p>}

        {!loading && !error && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {devices.length === 0 ? (
                  <p className="text-gray-400">You have no devices assigned to you.</p>
              ) : (
                  devices.map(device => (
                      <div key={device.id} className="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
                        <h3 className="text-xl font-semibold text-cyan-400">{device.name}</h3>
                        <p className="text-gray-300 mt-2">{device.description || 'No description'}</p>
                        <p className="text-gray-400 text-sm mt-1">{device.address}</p>
                        <div className="mt-4 pt-4 border-t border-gray-600">
                          <p className="text-sm text-gray-300">Max Consumption:
                            <span className="font-bold text-yellow-400 ml-2">{device.maxConsumption} kWh</span>
                          </p>
                        </div>
                      </div>
                  ))
              )}
            </div>
        )}
      </div>
  );
};

const AdminDashboard = ({ user, token, api }) => {
  const [users, setUsers] = useState([]);
  const [devices, setDevices] = useState([]);
  const [view, setView] = useState('users');
  const [apiError, setApiError] = useState(null);

  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState(null);

  const [isDeviceModalOpen, setIsDeviceModalOpen] = useState(false);
  const [editingDevice, setEditingDevice] = useState(null);

  // --- Logic for Users (Neschimbat) ---
  const fetchUsers = useCallback(async () => {
    try {
      const response = await api.get('/users');
      if (response.ok) {
        setUsers(await response.json());
      } else {
        console.error("Failed to fetch users");
      }
    } catch (e) {
      console.error(e);
    }
  }, [api]);

  const handleDeleteUser = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        const response = await api.delete(`/users/${userId}`);
        if (response.ok) {
          fetchUsers();
        } else {
          alert('Failed to delete user.');
        }
      } catch (e) {
        console.error(e);
      }
    }
  };

  // --- (ACTUALIZAT) Logic for User Modal ---
  const handleOpenUserModal = (user) => {
    setEditingUser(user || {});
    setApiError(null);
    setIsUserModalOpen(true);
  };

  const handleCloseUserModal = () => {
    setIsUserModalOpen(false);
    setEditingUser(null);
  };

  const handleSaveUser = async (userData) => {
    setApiError(null);
    const isEdit = !!userData.id;

    if (isEdit) {
      // --- Logic de EDITARE (rămâne la fel) ---
      try {
        const response = await api.put(`/users/${userData.id}`, userData);
        if (response.ok) {
          fetchUsers();
          handleCloseUserModal();
        } else {
          const err = await response.json();
          setApiError(err.message || 'Failed to update user.');
        }
      } catch (e) {
        setApiError(e.message);
      }
    } else {
      // --- Logică NOUĂ de CREARE (2 pași) ---
      try {
        // PASUL 1: Creează user-ul în user-service
        const userResponse = await api.post('/users', userData);

        if (!userResponse.ok) {
          const err = await userResponse.json();
          throw new Error(err.message || 'Failed to create user account.');
        }

        const createdUser = await userResponse.json();

        // PASUL 2: Setează parola în auth-service
        const authResponse = await api.post('/auth/register', {
          userId: createdUser.id,
          username: createdUser.username,
          role: createdUser.role,
          password: userData.password // Parola din formular
        });

        if (!authResponse.ok) {
          // Aici e o problemă: user-ul e creat, dar parola nu.
          // E o problemă de "saga/transacție", dar pentru acum, e suficient să informăm.
          throw new Error('User account was created, but failed to set password in auth-service.');
        }

        // Totul a mers bine!
        fetchUsers();
        handleCloseUserModal();

      } catch (e) {
        setApiError(e.message);
      }
    }
  };


  // --- Logic for Devices (Neschimbat) ---
  const fetchDevices = useCallback(async () => {
    try {
      const response = await api.get('/devices');
      if (response.ok) {
        setDevices(await response.json());
      } else {
        console.error("Failed to fetch devices");
      }
    } catch (e) {
      console.error(e);
    }
  }, [api]);

  const handleDeleteDevice = async (deviceId) => {
    if (window.confirm('Are you sure you want to delete this device?')) {
      try {
        const response = await api.delete(`/devices/${deviceId}`);
        if (response.ok) {
          fetchDevices();
        } else {
          alert('Failed to delete device.');
        }
      } catch (e) {
        console.error(e);
      }
    }
  };

  // --- (NOU) Logic for Device Modal ---
  const handleOpenDeviceModal = (device) => {
    setEditingDevice(device || {});
    setApiError(null);
    setIsDeviceModalOpen(true);
  };

  const handleCloseDeviceModal = () => {
    setIsDeviceModalOpen(false);
    setEditingDevice(null);
  };

  const handleSaveDevice = async (deviceData) => {
    setApiError(null);
    const isEdit = !!deviceData.id;
    const promise = isEdit
        ? api.put(`/devices/${deviceData.id}`, deviceData)
        : api.post('/devices', deviceData);

    try {
      const response = await promise;
      if (response.ok) {
        fetchDevices();
        handleCloseDeviceModal();
      } else {
        const err = await response.json();
        setApiError(err.message || (isEdit ? 'Failed to update device.' : 'Failed to create device.'));
      }
    } catch (e) {
      setApiError(e.message);
    }
  };

  // --- Effect to fetch data (Actualizat) ---
  useEffect(() => {
    // Întotdeauna avem nevoie de useri pentru formularul de device-uri
    fetchUsers();
    if (view === 'devices') {
      fetchDevices();
    }
  }, [view, fetchUsers, fetchDevices]);


  return (
      <div className="p-8">
        <h1 className="text-3xl font-bold mb-6">Welcome, {user.username}! (Admin)</h1>

        {/* Tab Navigation (Neschimbat) */}
        <div className="flex border-b border-gray-700 mb-6">
          <button
              onClick={() => setView('users')}
              className={`py-2 px-4 font-medium ${view === 'users' ? 'text-cyan-400 border-b-2 border-cyan-400' : 'text-gray-400'}`}
          >
            User Management
          </button>
          <button
              onClick={() => setView('devices')}
              className={`py-2 px-4 font-medium ${view === 'devices' ? 'text-cyan-400 border-b-2 border-cyan-400' : 'text-gray-400'}`}
          >
            Device Management
          </button>
        </div>

        {/* Content based on view (Actualizat) */}
        {view === 'users' && (
            <div>
              <h2 className="text-2xl font-semibold mb-4 text-cyan-400">Manage Users</h2>
              {/* (ACTUALIZAT) Butonul acum deschide modal-ul */}
              <button
                  onClick={() => handleOpenUserModal(null)}
                  className="bg-cyan-600 hover:bg-cyan-500 text-white font-bold py-2 px-4 rounded-lg mb-4"
              >
                Create New User
              </button>
              <div className="overflow-x-auto bg-gray-800 rounded-lg border border-gray-700">
                <table className="min-w-full">
                  <thead className="bg-gray-700">
                  <tr>
                    <th className="py-3 px-4 text-left">Username</th>
                    <th className="py-3 px-4 text-left">Role</th>
                    <th className="py-3 px-4 text-left">Full Name</th>
                    <th className="py-3 px-4 text-left">User ID</th>
                    <th className="py-3 px-4 text-left">Actions</th>
                  </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-700">
                  {users.map(u => (
                      <tr key={u.id}>
                        <td className="py-3 px-4">{u.username}</td>
                        <td className="py-3 px-4">
                      <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                          u.role === 'ADMIN' ? 'bg-green-700 text-green-100' : 'bg-blue-700 text-blue-100'
                      }`}>
                        {u.role}
                      </span>
                        </td>
                        <td className="py-3 px-4">{u.name}</td>
                        <td className="py-3 px-4 text-gray-400 text-xs">{u.id}</td>
                        <td className="py-3 px-4">
                          {/* (ACTUALIZAT) Butonul Edit deschide modal-ul */}
                          <button
                              onClick={() => handleOpenUserModal(u)}
                              className="text-yellow-400 hover:text-yellow-300 mr-2"
                          >
                            Edit
                          </button>
                          <button onClick={() => handleDeleteUser(u.id)} className="text-red-500 hover:text-red-400">Delete</button>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
            </div>
        )}

        {view === 'devices' && (
            <div>
              <h2 className="text-2xl font-semibold mb-4 text-cyan-400">Manage Devices</h2>
              {/* (ACTUALIZAT) Butonul acum deschide modal-ul */}
              <button
                  onClick={() => handleOpenDeviceModal(null)}
                  className="bg-cyan-600 hover:bg-cyan-500 text-white font-bold py-2 px-4 rounded-lg mb-4"
              >
                Create New Device
              </button>
              <div className="overflow-x-auto bg-gray-800 rounded-lg border border-gray-700">
                <table className="min-w-full">
                  <thead className="bg-gray-700">
                  <tr>
                    <th className="py-3 px-4 text-left">Device Name</th>
                    <th className="py-3 px-4 text-left">Address</th>
                    <th className="py-3 px-4 text-left">Max Consumption</th>
                    <th className="py-3 px-4 text-left">Assigned User ID</th>
                    <th className="py-3 px-4 text-left">Actions</th>
                  </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-700">
                  {devices.map(d => (
                      <tr key={d.id}>
                        <td className="py-3 px-4">{d.name}</td>
                        <td className="py-3 px-4">{d.address}</td>
                        <td className="py-3 px-4">{d.maxConsumption} kWh</td>
                        <td className="py-3 px-4 text-gray-400 text-xs">{d.userId}</td>
                        <td className="py-3 px-4">
                          {/* (ACTUALIZAT) Butonul Edit deschide modal-ul */}
                          <button
                              onClick={() => handleOpenDeviceModal(d)}
                              className="text-yellow-400 hover:text-yellow-300 mr-2"
                          >
                            Edit
                          </button>
                          <button onClick={() => handleDeleteDevice(d.id)} className="text-red-500 hover:text-red-400">Delete</button>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
            </div>
        )}

        {/* --- (NOU) Definirea Modalelor --- */}
        <Modal
            isOpen={isUserModalOpen}
            onClose={handleCloseUserModal}
            title={editingUser?.id ? 'Edit User' : 'Create New User'}
        >
          <UserForm
              initialUser={editingUser}
              onSubmit={handleSaveUser}
              onCancel={handleCloseUserModal}
              apiError={apiError}
          />
        </Modal>

        <Modal
            isOpen={isDeviceModalOpen}
            onClose={handleCloseDeviceModal}
            title={editingDevice?.id ? 'Edit Device' : 'Create New Device'}
        >
          <DeviceForm
              initialDevice={editingDevice}
              users={users}
              onSubmit={handleSaveDevice}
              onCancel={handleCloseDeviceModal}
              apiError={apiError}
          />
        </Modal>

      </div>
  );
};

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('jwtToken'));
  const [user, setUser] = useState(null);
  const [page, setPage] = useState('login'); // 'login', 'admin', 'client'
  const [error, setError] = useState(null);

  const api = {
    request: async (method, path, body = null) => {
      const headers = new Headers({
        'Content-Type': 'application/json',
      });
      if (token) {
        headers.append('Authorization', `Bearer ${token}`);
      }

      const options = {
        method,
        headers,
        body: body ? JSON.stringify(body) : null,
      };

      return fetch(`/api${path}`, options);
    },
    get: function(path) { return this.request('GET', path) },
    post: function(path, body) { return this.request('POST', path, body) },
    put: function(path, body) { return this.request('PUT', path, body) },
    delete: function(path) { return this.request('DELETE', path) },
  };

  useEffect(() => {
    if (token) {
      localStorage.setItem('jwtToken', token);
      const decodedUser = parseJwt(token);
      if (decodedUser) {
        setUser(decodedUser);
        if (decodedUser.role === 'ADMIN') {
          setPage('admin');
        } else if (decodedUser.role === 'CLIENT') {
          setPage('client');
        }
      } else {
        setToken(null);
        localStorage.removeItem('jwtToken');
        setPage('login');
      }
    } else {
      localStorage.removeItem('jwtToken');
      setUser(null);
      setPage('login');
    }
  }, [token]);

  // --- Handlers (Neschimbat) ---
  const handleLogin = async (username, password) => {
    setError(null);
    try {
      const response = await api.post('/auth/login', { username, password });

      if (!response.ok) {
        const errData = await response.json();
        throw new Error(errData.message || 'Login failed');
      }

      const data = await response.json();
      setToken(data.token);
    } catch (err) {
      console.error(err);
      setError(err.message || 'An unknown error occurred.');
    }
  };

  const handleLogout = () => {
    setToken(null);
  };

  // --- Render Logic (Neschimbat) ---
  const renderPage = () => {
    switch (page) {
      case 'admin':
        return <AdminDashboard user={user} token={token} api={api} />;
      case 'client':
        return <ClientDashboard user={user} token={token} api={api} />;
      case 'login':
      default:
        return <LoginPage onLogin={handleLogin} error={error} />;
    }
  };

  return (
      <div className="min-h-screen bg-gray-900 text-white font-sans">
        {user && (
            <nav className="bg-gray-800 p-4 shadow-lg flex justify-between items-center border-b border-gray-700">
              <div className="text-xl font-bold text-cyan-400">EMS</div>
              <div>
                <span className="mr-4 text-gray-300">Logged in as: {user.username} ({user.role})</span>
                <button
                    onClick={handleLogout}
                    className="bg-red-600 hover:bg-red-500 text-white font-bold py-2 px-4 rounded-lg"
                >
                  Logout
                </button>
              </div>
            </nav>
        )}

        <main>
          {renderPage()}
          {user && <ChatAndNotification user={user} />}
        </main>
      </div>
  );
}