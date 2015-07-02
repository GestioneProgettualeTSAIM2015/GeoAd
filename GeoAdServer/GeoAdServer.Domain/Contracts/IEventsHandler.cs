using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IEventsHandler
    {
        void Enqueue(IEvent command);

        IChangedPositionHandler ChpHandler { get; set; }

        IUserPreferencesHandler UserPrefsHandler { get; set; }

        IEnumerable<string> GetKeysAffected(double lat, double lng, int? locationId = null);
    }
}
