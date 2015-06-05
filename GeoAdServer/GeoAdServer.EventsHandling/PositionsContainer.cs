using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class PositionsContainer
    {
        private readonly static PositionsContainer _instance = new PositionsContainer();

        public static PositionsContainer Instance { get; set; }

        private ConcurrentDictionary<string, ChangedPosition> _positions;

        private PositionsContainer()
        {
            _positions = new ConcurrentDictionary<string, ChangedPosition>();
        }

        public void Handle(IEvent ievent)
        {
            if (ievent is ChangedPosition)
            {
                var chp = ievent as ChangedPosition;
                ChangedPosition oldChp;
                _positions.TryGetValue(chp.Key, out oldChp);
                _positions.TryAdd(chp.Key, chp);
            }
        }

        public IEnumerable<string> GetKeysAffected(double lat, double lng)
        {
            return
                from pair in _positions
		        where Double.Parse(pair.Value.NWCoord.Lat) > lat &&
                      Double.Parse(pair.Value.SECoord.Lat) < lat &&
                      Double.Parse(pair.Value.SECoord.Lng) > lng &&
                      Double.Parse(pair.Value.NWCoord.Lng) < lng
		        select pair.Key;
        }
    }
}
