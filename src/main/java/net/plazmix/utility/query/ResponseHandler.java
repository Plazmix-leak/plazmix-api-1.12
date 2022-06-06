package net.plazmix.utility.query;

public interface ResponseHandler<R, O> {

    R handleResponse(O o);
}
