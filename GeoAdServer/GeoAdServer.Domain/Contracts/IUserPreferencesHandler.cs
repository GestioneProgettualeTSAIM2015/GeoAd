﻿using GeoAdServer.Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IUserPreferencesHandler : IDisposable
    {
        IEnumerable<string> GetKeys(int locationId, PreferenceTypes pref);

        bool SetPreference(int locationId, string key, PreferenceTypes pref);

        bool DeletePreference(int locationId, string key, PreferenceTypes pref);

        Dictionary<PreferenceTypes, List<int>> Get(string key);

        bool Delete(string key);
    }
}
