package com.money.manager.api;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class HateoasUtils {

    static URI relativeHref(Object invocationValue) {
        URI absoluteUri = ControllerLinkBuilder.linkTo(invocationValue).toUri();
        return UriComponentsBuilder
                .fromPath(absoluteUri.getPath())
                .query(absoluteUri.getQuery())
                .fragment(absoluteUri.getFragment())
                .build()
                .toUri();
    }
}
