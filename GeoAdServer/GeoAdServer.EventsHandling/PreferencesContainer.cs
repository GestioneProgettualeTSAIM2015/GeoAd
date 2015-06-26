using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class PreferencesContainer : IUserPreferencesHandler
    {
        private readonly static int _LOCAL_CHANGES_LIMIT = 1;

        private IPreferencesRepository _preferencesRepository;
        private IPreferencesRepository PreferencesRepository {
            get
            {
                return _preferencesRepository;
            }
            set
            {
                _preferencesRepository = value;
                if (_preferencesRepository != null) _preferences = _preferencesRepository.Get();
                else _preferences = new Preferences();
            }
        }

        private Preferences _preferences;
        private int _localChangesCount;

        public PreferencesContainer(IPreferencesRepository preferencesRepository = null)
        {
            PreferencesRepository = preferencesRepository;
            _localChangesCount = 0;
        }

        bool IUserPreferencesHandler.SetPreference(int locationId, string key, PreferenceTypes pref)
        {
            foreach (PreferenceTypes prefType in Enum.GetValues(typeof(PreferenceTypes)))
            {
                if (prefType == pref) continue;
                _preferences.RemoveIfContains(locationId, key, prefType);
            }

            bool result;
            if (result = _preferences.Add(locationId, key, pref)) IncrLocalChanges();
            return result;
        }

        bool IUserPreferencesHandler.DeletePreference(int locationId, string key, PreferenceTypes pref)
        {
            bool result;
            if (result = _preferences.RemoveIfContains(locationId, key, pref)) IncrLocalChanges();
            return result;
        }

        IEnumerable<string> IUserPreferencesHandler.GetKeys(int locationId, PreferenceTypes pref)
        {
            return _preferences.GetSubscribedKeysOrInit(locationId, pref).AsEnumerable();
        }

        private void IncrLocalChanges(int changes = 1)
        {
            if (++_localChangesCount >= _LOCAL_CHANGES_LIMIT)
            {
                _localChangesCount = 0;
                if (PreferencesRepository != null)
                {
                    PreferencesRepository.Set(_preferences);
                }
            }
        }

        Dictionary<PreferenceTypes, List<int>> IUserPreferencesHandler.Get(string key)
        {
            var keyPrefs = new Dictionary<PreferenceTypes, List<int>>();

            foreach (var locationPair in _preferences.RawDataType)
            {
                foreach (var prefTypePair in locationPair.Value)
                {
                    if (prefTypePair.Value.Contains(key))
                    {
                        List<int> ids = null;
                        if (!keyPrefs.TryGetValue(prefTypePair.Key, out ids))
                        {
                            ids = new List<int>();
                            keyPrefs[prefTypePair.Key] = ids;
                        }

                        ids.Add(locationPair.Key);
                    }
                }
            }

            return keyPrefs;
        }

        bool IUserPreferencesHandler.Delete(string key)
        {
            bool keyFound = false;

            var keyPrefs = new Dictionary<PreferenceTypes, List<int>>();

            foreach (var locationPair in _preferences.RawDataType)
            {
                foreach (var prefTypePair in locationPair.Value)
                {
                    if (prefTypePair.Value.Remove(key))
                        keyFound = true;
                }
            }

            IncrLocalChanges();
            return keyFound;
        }

        void IDisposable.Dispose()
        {
            _preferences = new Preferences();
            PreferencesRepository.Dispose();
        }
    }
}
