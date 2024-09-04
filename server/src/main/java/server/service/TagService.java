package server.service;

import java.util.List;

import commons.dtos.TagDTO;

public interface TagService {

    /**
     * Returns all tags associated with an event
     *
     * @param eventId of the tags
     * @return the associated tags
     */
    List<TagDTO> getAllTags(long eventId);

    /**
     * Returns the tag with the associated id
     *
     * @param tagId id of the requested tag
     * @return the requested tag
     */
    TagDTO getTagById(long tagId);

    /**
     * Creates a tag with the provided object and in the specified eventId
     *
     * @param eventId id of the to create the tag in
     * @param newTag  body of new tag to create
     * @return returns the tagDTO for the new tag
     */
    TagDTO createTag(long eventId, TagDTO newTag);

    /**
     * Edits a tag with the corresponding id
     *
     * @param eventId id of the event
     * @param tagId   of the tag to be edited
     * @param newTag  new info for the tag
     * @return returns the tagDTO for the editeg tag
     */
    TagDTO editTag(long eventId, long tagId, TagDTO newTag);

    /**
     * Deletes a tag with that id
     *
     * @param eventId id of the event
     * @param tagId   id of the deleted tag
     */
    void deleteTag(long eventId, long tagId);
}
