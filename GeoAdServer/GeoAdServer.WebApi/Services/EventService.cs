using GeoAdServer.Domain.Contracts;
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

        private EventService()
        {
            _eventsHandlers = new LinkedList<IEventsHandler>();
        }

        public void Add(IEventsHandler eventsHandler)
        {
            _eventsHandlers.Add(eventsHandler);
            if (_chpHandler != null) eventsHandler.ChpHandler = _chpHandler;
        }

        public void Remove(IEventsHandler eventsHandler)
        {
            _eventsHandlers.Remove(eventsHandler);
        }

        public void SetPositionsContainer(IChangedPositionHandler chHandler)
        {
            _chpHandler = chHandler;
            foreach (IEventsHandler eventHandler in _eventsHandlers)
            {
                eventHandler.ChpHandler = _chpHandler;
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

        public IEnumerable<string> GetKeysAffected(double lat, double lng)
        {
            if (_chpHandler == null) return null;

            return _chpHandler.GetKeysAffected(lat, lng);
        }
    }
}