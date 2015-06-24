using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities
{
    [Serializable]
    public class Preferences
    {
        public ConcurrentDictionary<int, Dictionary<PreferenceTypes, List<string>>> RawDataType { get; private set; }

        public Preferences()
        {
            RawDataType = new ConcurrentDictionary<int, Dictionary<PreferenceTypes, List<string>>>();
        }

        public void Add(int locationId, string key, PreferenceTypes pref)
        {
            GetSubscribedKeysOrInit(locationId, pref).Add(key);
        }

        public bool RemoveIfContains(int locationId, string key, PreferenceTypes pref)
        {
            Dictionary<PreferenceTypes, List<string>> subPrefs = null;
            List<string> subscribedKeys = null;
            if (!RawDataType.TryGetValue(locationId, out subPrefs))
            {
                return false;
            }
            else if (!subPrefs.TryGetValue(pref, out subscribedKeys))
            {
                return false;
            }

            return subscribedKeys.Remove(key);
        }

        public List<string> GetSubscribedKeysOrInit(int locationId, PreferenceTypes pref)
        {
            Dictionary<PreferenceTypes, List<string>> subPrefs = null;
            List<string> subscribedKeys = null;
            if (!RawDataType.TryGetValue(locationId, out subPrefs))
            {
                subPrefs = new Dictionary<PreferenceTypes, List<string>>();
                subscribedKeys = new List<string>();
                subPrefs[pref] = subscribedKeys;
                RawDataType[locationId] = subPrefs;
            }
            else if (!subPrefs.TryGetValue(pref, out subscribedKeys))
            {
                subscribedKeys = new List<string>();
                subPrefs[pref] = subscribedKeys;
            }

            return subscribedKeys;
        }
    }
}
