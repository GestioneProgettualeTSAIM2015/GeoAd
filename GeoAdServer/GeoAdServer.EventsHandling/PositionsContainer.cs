using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class PositionsContainer : IChangedPositionHandler
    {
        private ConcurrentDictionary<string, ChangedPosition> _positions;

        public PositionsContainer()
        {
            _positions = new ConcurrentDictionary<string, ChangedPosition>();
        }

        void IChangedPositionHandler.Handle(ChangedPosition chp)
        {
            ChangedPosition oldChp;
            _positions.TryRemove(chp.Key, out oldChp);
            _positions.TryAdd(chp.Key, chp);
        }

        IEnumerable<string> IChangedPositionHandler.GetKeysAffected(double lat, double lng)
        {
            return
                from pair in _positions
                where double.Parse(pair.Value.NWCoord.Lat, CultureInfo.InvariantCulture) > lat &&
                      double.Parse(pair.Value.SECoord.Lat, CultureInfo.InvariantCulture) < lat &&
                      double.Parse(pair.Value.SECoord.Lng, CultureInfo.InvariantCulture) > lng &&
                      double.Parse(pair.Value.NWCoord.Lng, CultureInfo.InvariantCulture) < lng
                select pair.Key;
        }
    }
}
