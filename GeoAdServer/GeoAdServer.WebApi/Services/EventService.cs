using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace GeoAdServer.WebApi.Services
{
    public class EventService
    {
        private static readonly EventService _instance = new EventService();

        public static EventService Instance {
            get
            {
                return _instance;
            }
        }

        private ICollection<IEventsHandler> _eventsHandlers;
        private IChangedPositionHandler _chpHandler;
        private IUserPreferencesHandler _userPrefsHandler;

        private EventService()
        {
            _eventsHandlers = new LinkedList<IEventsHandler>();
        }

        public void Add(IEventsHandler eventsHandler)
        {
            _eventsHandlers.Add(eventsHandler);
            if (_chpHandler != null)
            {
                eventsHandler.ChpHandler = _chpHandler;
                eventsHandler.UserPrefsHandler = _userPrefsHandler;
            }
        }

        public void Remove(IEventsHandler eventsHandler)
        {
            _eventsHandlers.Remove(eventsHandler);
        }

        public void SetPositionsContainer(IChangedPositionHandler chpHandler)
        {
            _chpHandler = chpHandler;
            foreach (IEventsHandler eventHandler in _eventsHandlers)
            {
                eventHandler.ChpHandler = _chpHandler;
            }
        }

        public void SetUserPreferencesHandler(IUserPreferencesHandler userPrefsHandler)
        {
            _userPrefsHandler = userPrefsHandler;
            foreach (IEventsHandler eventHandler in _eventsHandlers)
            {
                eventHandler.UserPrefsHandler = _userPrefsHandler;
            }
        }

        public void Enqueue(IEvent command)
        {
            foreach (IEventsHandler eventHandler in _eventsHandlers)
            {
                eventHandler.Enqueue(command);
            }
        }

        public void HandleChangedPosition(ChangedPosition chp)
        {
            if (_chpHandler != null)
            {
                _chpHandler.Handle(chp);
            }
        }

        public void SetPreference(int locationId, string key, PreferenceTypes pref)
        {
            _userPrefsHandler.SetPreference(locationId, key, pref);
        }

        public void DeletePreference(int locationId, string key, PreferenceTypes pref)
        {
            _userPrefsHandler.DeletePreference(locationId, key, pref);
        }

        public Dictionary<PreferenceTypes, List<int>> Get(string key)
        {
            return _userPrefsHandler.Get(key);
        }

        public bool Delete(string key)
        {
            return _userPrefsHandler.Delete(key);
        }
    }
}