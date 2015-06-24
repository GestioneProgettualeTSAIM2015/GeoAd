using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.Events;
using System.Collections.Generic;
using System.Linq;

namespace GeoAdServer.Domain.Contracts
{
    public abstract class AbstractEventsHandler : IEventsHandler
    {
        public abstract void Enqueue(IEvent command);

        IChangedPositionHandler IEventsHandler.ChpHandler { get; set; }

        IUserPreferencesHandler IEventsHandler.UserPrefsHandler { get; set; }

        IEnumerable<string> IEventsHandler.GetKeysAffected(double lat, double lng, int? locationId)
        {
            var me = ((IEventsHandler)this);
            if (me.ChpHandler == null) return null;

            HashSet<string> keysSet = new HashSet<string>(me.ChpHandler.GetKeysAffected(lat, lng)); //geo keys

            if (me.UserPrefsHandler != null)
            {
                if (locationId.HasValue)
                {
                    keysSet.UnionWith(me.UserPrefsHandler.GetKeys(locationId.Value, PreferenceTypes.FAVORITE)); //add favorites
                }

                keysSet.ExceptWith(me.UserPrefsHandler.GetKeys(locationId.Value, PreferenceTypes.IGNORED)); //remove ignored
            }

            return keysSet.AsEnumerable();
        }
    }
}
