using GeoAdServer.Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IPreferencesRepository : IDisposable
    {
        Preferences Get();

        bool Set(Preferences prefs);
    }
}
