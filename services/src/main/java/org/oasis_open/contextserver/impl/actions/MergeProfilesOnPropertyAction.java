package org.oasis_open.contextserver.impl.actions;

import org.oasis_open.contextserver.api.Event;
import org.oasis_open.contextserver.api.Persona;
import org.oasis_open.contextserver.api.Session;
import org.oasis_open.contextserver.api.Profile;
import org.oasis_open.contextserver.api.actions.Action;
import org.oasis_open.contextserver.api.actions.ActionExecutor;
import org.oasis_open.contextserver.api.services.ProfileService;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MergeProfilesOnPropertyAction implements ActionExecutor {

    private final int MAX_COOKIE_AGE_IN_SECONDS = 60 * 60 * 24 * 365 * 10; // 10-years
    private int cookieAgeInSeconds = MAX_COOKIE_AGE_IN_SECONDS;
    private String profileIdCookieName = "context-profile-id";

    private ProfileService profileService;

    public void setCookieAgeInSeconds(int cookieAgeInSeconds) {
        this.cookieAgeInSeconds = cookieAgeInSeconds;
    }

    public void setProfileIdCookieName(String profileIdCookieName) {
        this.profileIdCookieName = profileIdCookieName;
    }

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public boolean execute(Action action, Event event) {
        String mergeProfilePropertyName = (String) action.getParameterValues().get("mergeProfilePropertyName");
        Profile profile = event.getProfile();

        if (profile instanceof Persona) {
            return false;
        }

        Object currentMergePropertyValue = profile.getProperty(mergeProfilePropertyName);

        if (currentMergePropertyValue == null) {
            return false;
        }
        String profileId = profile.getItemId();
        boolean updated = profileService.mergeProfilesOnProperty(profile, event.getSession(), mergeProfilePropertyName, (currentMergePropertyValue == null ? null : currentMergePropertyValue.toString()));

        if (!event.getSession().getProfileId().equals(profileId)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) event.getAttributes().get(Event.HTTP_RESPONSE_ATTRIBUTE);
            sendProfileCookie(event.getSession().getProfile(), httpServletResponse);
        }

        return updated;
    }

    public void sendProfileCookie(Profile profile, ServletResponse response) {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            Cookie profileIdCookie = new Cookie(profileIdCookieName, profile.getItemId());
            profileIdCookie.setPath("/");
            profileIdCookie.setMaxAge(cookieAgeInSeconds);
            httpServletResponse.addCookie(profileIdCookie);
        }
    }

}
